package lab2.models;

public class MatrixFragment {
	
	private int rowIndex;
	private int colIndex;
	private double[] row;
	private double[] col;
	
	private boolean calculated;
	
	public MatrixFragment(int rowIndex, int colIndex, double[] row, double[] col) {
		this.rowIndex = rowIndex;
		this.colIndex = colIndex;
		this.row = row;
		this.col = col;
		this.setCalculated(false);
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

	public boolean isCalculated() {
		return calculated;
	}

	public void setCalculated(boolean calculated) {
		this.calculated = calculated;
	}
	
	public int getSize() {
		if (row.length == col.length)
			return row.length;
		
		return 0;
	}
}