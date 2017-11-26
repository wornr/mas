package lab2.models;

import java.io.Serializable;

import lab2.enums.MatrixFragmentState;

public class MatrixFragment implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7521433019960706969L;
	
	private int rowIndex;
	private int colIndex;
	private double[] row;
	private double[] col;
	
	private double result;
	private MatrixFragmentState state;
		
	public MatrixFragment(int rowIndex, int colIndex, double[] row, double[] col) {
		this.rowIndex = rowIndex;
		this.colIndex = colIndex;
		this.row = row;
		this.col = col;
		this.state = MatrixFragmentState.InQueue;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public int getColIndex() {
		return colIndex;
	}

	public double[] getRow() {
		return row;
	}

	public double[] getCol() {
		return col;
	}
	
	public double getResult() {
		return result;
	}
	
	public void setResult(double result) {
		this.result = result;
		this.state = MatrixFragmentState.Calculated;
	}

	public MatrixFragmentState getState() {
		return state;
	}
	
	public void setState(MatrixFragmentState state) {
		this.state = state;
	}
	
	public int getSize() {
		if (row.length == col.length)
			return row.length;
		
		return 0;
	}
}