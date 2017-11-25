package lab2.helpers;

import java.util.ArrayList;
import java.util.List;

import lab2.models.Matrix;
import lab2.models.MatrixFragment;

public class MatrixFragmentGenerator {
	
	private static MatrixFragmentGenerator instance = null;
	
	protected MatrixFragmentGenerator() {}
	
	public static MatrixFragmentGenerator getInstance() {
		if (instance == null) {
			instance = new MatrixFragmentGenerator();
		}
		
		return instance;
	}
	
	public List<MatrixFragment> generateFragments(Matrix a, Matrix b) {
		List<MatrixFragment> matrixFragments = new ArrayList<>();
		
		for (int i = 0; i < a.getRows(); i++) {
			for (int j = 0; j < b.getColumns(); j++) {
				double[] transpValues = new double[b.getRows()];
						
				for (int k = 0; k < b.getRows(); k++) {
					 transpValues[k] = b.getValues()[k][j];
				}
				
				matrixFragments.add(new MatrixFragment(i, j, a.getValues()[i], transpValues));
			}
		}
		
		return matrixFragments;
	}
}