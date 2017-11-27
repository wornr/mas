package lab2.models;

public class Matrix {
	
	private int rows;
	private int columns;
	private double[][] values;
	
	public Matrix(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		this.values = new double[rows][columns];
	}
	
	public Matrix(int rows, int columns, double[][] values) {
		this.rows = rows;
		this.columns = columns;
		this.values = values;
	}
	
	@Override
	public String toString() {
		StringBuilder matrixStringRepresentation = new StringBuilder();
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				matrixStringRepresentation.append(values[i][j]);
				matrixStringRepresentation.append(" ");
			}
			matrixStringRepresentation.append("\n");
		}
		
		return matrixStringRepresentation.toString();
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	public double[][] getValues() {
		return values;
	}
	
	public void setValue(int row, int col, double value) {
		this.values[row][col] = value;
	}
}