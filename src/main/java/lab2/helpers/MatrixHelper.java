package lab2.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import lab2.agents.DistributorAgent;
import lab2.models.Matrix;
import lab2.models.MatrixFragment;

public class MatrixHelper {
	
	private static MatrixHelper instance = null;
	
	protected MatrixHelper() {}
	
	public static MatrixHelper getInstance() {
		if (instance == null) {
			instance = new MatrixHelper();
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
	
	public Matrix readMatrixFile(String fileName) {
		try (Scanner fileIn = new Scanner(new File(DistributorAgent.class.getClassLoader().getResource(fileName).toString().substring(6)))) {
			int columns = fileIn.nextInt();
			int rows = fileIn.nextInt();
			double[][] values = new double[rows][columns];
			
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					values[i][j] = fileIn.nextDouble();
				}
			}
			
			return new Matrix(rows, columns, values);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;		
	}
	
	public Matrix prepareResultMatrix(Matrix a, Matrix b) {
		return new Matrix(a.getRows(), b.getColumns());
	}
}