package jmini3d.demo.gvr;

import com.google.vr.sdk.base.Distortion;
import com.google.vr.sdk.base.FieldOfView;
import com.google.vr.sdk.base.GvrViewerParams;
import com.google.vr.sdk.base.ScreenParams;

/**
 * Class to calculate the correction for the pincushion lens distortion of the VR headsets
 * Code converted from the gvr-unity-sdk https://github.com/googlevr/gvr-unity-sdk
 */
public class PincushionUtils {

	// Solves a small linear equation via destructive gaussian
	// elimination and back substitution.  This isn't generic numeric
	// code, it's just a quick hack to work with the generally
	// well-behaved symmetric matrices for least-squares fitting.
	// Not intended for reuse.
	//
	// @param a Input positive definite symmetrical matrix. Destroyed
	//     during calculation.
	// @param y Input right-hand-side values. Destroyed during calculation.
	// @return Resulting x value vector.
	//
	private static double[] solveLinear(double[][] a, double[] y) {
		int n = a[0].length;

		// Gaussian elimination (no row exchange) to triangular matrix.
		// The input matrix is a A^T A product which should be a positive
		// definite symmetrical matrix, and if I remember my linear
		// algebra right this implies that the pivots will be nonzero and
		// calculations sufficiently accurate without needing row
		// exchange.
		for (int j = 0; j < n - 1; ++j) {
			for (int k = j + 1; k < n; ++k) {
				double p = a[k][j] / a[j][j];
				for (int i = j + 1; i < n; ++i) {
					a[k][i] -= p * a[j][i];
				}
				y[k] -= p * y[j];
			}
		}
		// From this point on, only the matrix elements a[j][i] with i>=j are
		// valid. The elimination doesn't fill in eliminated 0 values.

		double[] x = new double[n];

		// Back substitution.
		for (int j = n - 1; j >= 0; --j) {
			double v = y[j];
			for (int i = j + 1; i < n; ++i) {
				v -= a[j][i] * x[i];
			}
			x[j] = v / a[j][j];
		}

		return x;
	}

	// Solves a least-squares matrix equation.  Given the equation A * x = y, calculate the
	// least-square fit x = inverse(A * transpose(A)) * transpose(A) * y.  The way this works
	// is that, while A is typically not a square matrix (and hence not invertible), A * transpose(A)
	// is always square.  That is:
	//   A * x = y
	//   transpose(A) * (A * x) = transpose(A) * y   <- multiply both sides by transpose(A)
	//   (transpose(A) * A) * x = transpose(A) * y   <- associativity
	//   x = inverse(transpose(A) * A) * transpose(A) * y  <- solve for x
	// Matrix A's row count (first index) must match y's value count.  A's column count (second index)
	// determines the length of the result vector x.
	private static double[] solveLeastSquares(double[][] matA, double[] vecY) {
		int numSamples = matA.length;
		int numCoefficients = matA[0].length;
		if (numSamples != vecY.length) {
			System.out.println("Matrix / vector dimension mismatch");
			return null;
		}

		// Calculate transpose(A) * A
		double[][] matATA = new double[numCoefficients][numCoefficients];
		for (int k = 0; k < numCoefficients; ++k) {
			for (int j = 0; j < numCoefficients; ++j) {
				double sum = 0.0;
				for (int i = 0; i < numSamples; ++i) {
					sum += matA[i][j] * matA[i][k];
				}
				matATA[j][k] = sum;
			}
		}

		// Calculate transpose(A) * y
		double[] vecATY = new double[numCoefficients];
		for (int j = 0; j < numCoefficients; ++j) {
			double sum = 0.0;
			for (int i = 0; i < numSamples; ++i) {
				sum += matA[i][j] * vecY[i];
			}
			vecATY[j] = sum;
		}

		// Now solve (A * transpose(A)) * x = transpose(A) * y.
		return solveLinear(matATA, vecATY);
	}

	/// Calculates an approximate inverse to the given radial distortion parameters.
	public static float[] approximateInverse(Distortion distort) {
		float maxRadius = 1;
		int numSamples = 100;
		final int numCoefficients = 6;

		// R + K1*R^3 + K2*R^5 = r, with R = rp = distort(r)
		// Repeating for numSamples:
		//   [ R0^3, R0^5 ] * [ K1 ] = [ r0 - R0 ]
		//   [ R1^3, R1^5 ]   [ K2 ]   [ r1 - R1 ]
		//   [ R2^3, R2^5 ]            [ r2 - R2 ]
		//   [ etc... ]                [ etc... ]
		// That is:
		//   matA * [K1, K2] = y
		// Solve:
		//   [K1, K2] = inverse(transpose(matA) * matA) * transpose(matA) * y
		double[][] matA = new double[numSamples][numCoefficients];
		double[] vecY = new double[numSamples];
		for (int i = 0; i < numSamples; ++i) {
			float r = maxRadius * (i + 1) / (float) numSamples;
			double rp = distort.distort(r);
			double v = rp;
			for (int j = 0; j < numCoefficients; ++j) {
				v *= rp * rp;
				matA[i][j] = v;
			}
			vecY[i] = r - rp;
		}
		double[] vecK = solveLeastSquares(matA, vecY);
		// Convert to float for use in a fresh Distortion object.
		float[] coefficients = new float[vecK.length];
		for (int i = 0; i < vecK.length; ++i) {
			coefficients[i] = (float) vecK[i];
		}
		return coefficients;
	}

	// Calculates the tan-angles from the maximum FOV for the left eye for the
	// current device and screen parameters.
	public static float getMaxRadius(ScreenParams screenParams, GvrViewerParams gvrViewParams) {
		// Tan-angles from the max FOV
		FieldOfView leftEyeMaxFov = gvrViewParams.getLeftEyeMaxFov();
		float fovLeft = (float) Math.tan(leftEyeMaxFov.getLeft() * Math.PI / 180f);
		float fovTop = (float) Math.tan(leftEyeMaxFov.getTop() * Math.PI / 180f);
		float fovRight = (float) Math.tan(leftEyeMaxFov.getRight() * Math.PI / 180f);
		float fovBottom = (float) Math.tan(leftEyeMaxFov.getBottom() * Math.PI / 180f);
		// Viewport size.
		float halfWidth = screenParams.getWidth() / 4;
		float halfHeight = screenParams.getHeight() / 2;
		// Viewport center, measured from left lens position.
		float centerX = gvrViewParams.getInterLensDistance() / 2 - halfWidth;
		float centerY = -gvrViewParams.getVerticalDistanceToLensCenter();
		float centerZ = gvrViewParams.getScreenToLensDistance();
		// Tan-angles of the viewport edges, as seen through the lens.
		float screenLeft = gvrViewParams.getDistortion().distort((centerX - halfWidth) / centerZ);
		float screenTop = gvrViewParams.getDistortion().distort((centerY + halfHeight) / centerZ);
		float screenRight = gvrViewParams.getDistortion().distort((centerX + halfWidth) / centerZ);
		float screenBottom = gvrViewParams.getDistortion().distort((centerY - halfHeight) / centerZ);
		// Compare the two sets of tan-angles and take the value closer to zero on each side.
		float[] tanAngles = {
				Math.max(fovLeft, screenLeft),
				Math.min(fovTop, screenTop),
				Math.min(fovRight, screenRight),
				Math.max(fovBottom, screenBottom)
		};

		return getMaxRadius(tanAngles);
	}

	public static float getMaxRadius(float[] tanAngleRect) {
		float x = Math.max(Math.abs(tanAngleRect[0]), Math.abs(tanAngleRect[2]));
		float y = Math.max(Math.abs(tanAngleRect[1]), Math.abs(tanAngleRect[3]));
		return (float) Math.sqrt(x * x + y * y);
	}
}