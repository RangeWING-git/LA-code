import java.util.LinkedList;
import java.util.Scanner;
import java.util.StringTokenizer;

import exception.InputException;


public class Matrix implements LAObject{
	private LinkedList<double[]> matrix;
	int m, n;

	public Matrix(LinkedList<double[]> matrix, int m, int n){
		this.matrix = matrix;
		this.m = m;
		this.n = n;		
	}

	public Matrix clone(){
		LinkedList<double[]> list = new LinkedList<double[]>();
		double[] buffer;
		for(int i=0; i<m; i++){
			buffer = new double[n];
			for(int j=0; j<n; j++){
				buffer[j] = matrix.get(i)[j];
			}
			list.add(buffer);
		}
		return new Matrix(list, m, n);
	}
	
	public void info(){
		System.out.println("a " + m + "×" + n + " matrix.");
	}
	
	public void print(){
		//System.out.println("This matrix is");
		double buffer;
		for(int i=0; i<m; i++){
			for(int j=0; j<n; j++){
				if(isInteger(buffer = matrix.get(i)[j])){
					System.out.print((int)buffer);
				}else{
					System.out.print(buffer);
				}
				System.out.print(" ");
			}
			System.out.println();
		}
	}
	
	public Matrix add(Matrix mat) throws Exception{
		if((m != mat.m) || (n != mat.n)){
			throw new Exception("The two matrices don't have the same size");
		}
		
		LinkedList<double[]> result = new LinkedList<double[]>();
		double[] buffer;
		for(int i=0; i<m; i++){
			buffer = new double[n];
			for(int j=0; j<n; j++){
				buffer[j] = matrix.get(i)[j] + mat.get(i, j);
			}
			result.add(buffer);
		}
		return new Matrix(result, m, n);
	}
	
	public Matrix multi(double multi){
		LinkedList<double[]> result = new LinkedList<double[]>();
		double[] buffer;
		for(int i=0; i<m; i++){
			buffer = new double[n];
			for(int j=0; j<n; j++){
				buffer[j] = matrix.get(i)[j] * multi;
			}
			result.add(buffer);
		}
		return new Matrix(result, m, n);
	}
	
	public static Matrix cross(Matrix mat1, Matrix mat2) throws Exception{
		if((mat1.n != mat2.m)){
			throw new Exception("The two matrices can't be multipied.");
		}
		LinkedList<double[]> result = new LinkedList<double[]>();
		double[] buffer;
		for(int i=0; i<mat1.m; i++){
			buffer = new double[mat2.n];
			for(int j=0; j<mat2.n; j++){
				buffer[j] = dot(mat1.getRow(i), mat2.getColumn(j));
			}
			result.add(buffer);
		}
		return new Matrix(result, mat1.m, mat2.n);
	}
	
	public static double dot(double[] mat1, double[] mat2) throws Exception{
		double result = 0;
		if(mat1.length != mat2.length){
			throw new Exception("The two matrices(vectors) can't be multipied since their size are not same.");
		}
		for(int i=0; i<mat1.length; i++){
			result += (mat1[i]*mat2[i]);
		}
		return result;
	}
	public static double dot(Matrix mat1, Matrix mat2) throws Exception{
		double result = 0;
		if((mat1.m == mat2.m) && (mat1.n == 1) & (mat2.n == 1)){
			for(int i=0; i<mat1.m; i++){
				result += (mat1.get(i, 0) * mat2.get(i, 0));
			}
		}else if((mat1.n == mat2.n) && (mat1.m == 1) & (mat2.m == 1)){
			for(int i=0; i<mat1.m; i++){
				result += (mat1.get(0, i) * mat2.get(0, i));
			}
		}else{
			throw new Exception("The two matrices(vectors) can't be multipied since their size are not same.");
		}
		return result;
	}
	
	public void switchRow(int row1, int row2){
		double[] _row1 = matrix.get(row1);
		matrix.remove(row1);
		matrix.add(row1, matrix.get(row2-1));
		matrix.remove(row2);
		matrix.add(row2, _row1);
	}
	
	public void multiRow(int row, double multi){
		double[] _row = matrix.get(row);
		for(int i=0; i<n; i++){
			_row[i] *= multi;
		}
		matrix.remove(row);
		matrix.add(row, _row);
	}
	
	public void addRow(int row1, int row2){
		addRow(row1, row2, 1);
	}
	public void addRow(int row1, int row2, double multi){
		double[] _row1 = matrix.get(row1);
		double[] _row2 = matrix.get(row2);
		for(int i=0; i<n; i++){
			_row2[i] += (_row1[i] * multi);
		}
		matrix.remove(row2);
		matrix.add(row2, _row2);
	}
	
	public void gelim(){
		boolean switched = false;
		int lastl1index = 0;
		
		for(int i=0; i<m; i++){
			//위치에 0이 있으면
			if(matrix.get(i)[lastl1index] == 0){
				switched = false;
				//nonzero row와 바꾼다
				for(int j=i+1; j<m; j++){
					if(matrix.get(j)[lastl1index] != 0){
						switchRow(i, j);
						switched = true;
						lastl1index++;
						break;
					}
				}
				if(switched == false){
					lastl1index++;
					i--;
					if(lastl1index >= m){
						break;
					}
					continue;
				}
			}
			
			double buffer = 0;
			int k;
			for(k=0; k<n; k++){
				if((buffer = matrix.get(i)[k]) != 0){
					multiRow(i, 1/buffer);
					break;
				}
			}
			for(int j=i+1; j<m; j++){
				if((buffer = matrix.get(j)[k]) != 0){
					addRow(i, j, -buffer);
				}
			}
			
			
		}
	}
	
	public void rref(){
		
		gelim();
		double buffer = 0;
		int k;
		for(int i=m-1; i>=0; i--){
			for(k=0; k<n; k++){
				if((buffer = matrix.get(i)[k]) != 0){
					break;
				}
			}
			if(k==3) continue;
			if(buffer != 1){
				multiRow(i, 1/buffer);
			}
			for(int j=i-1; j>=0; j--){
				if((buffer = matrix.get(j)[k]) != 0){
					addRow(i, j, -buffer);
				}
			}
		}
	}
	
	public boolean isInteger(double n){
		return (n == Math.floor(n));
	}
	
	/**
	 * inputMatrix
	 * 행렬을 받는다.
	 * @return Matrix
	 */
	public static Matrix inputMatrix(){
		Scanner sc = LinearAlgebra.sc;	//입력 받을 Scanner
		LinkedList<double[]> list = new LinkedList<double[]>(); 	//결과 list
		StringTokenizer st;	//원소를 구분할 토크나이저
		String line = ""; // 한 라인(row) 받을 스트링
		double[] lineBuffer;
		int m = 0;
		int n = -1;
		
		System.out.println("Input Matrix");
		
		//line에 한 라인 대입하고 END 아니면 계속 실행
		while((line = sc.nextLine()).compareTo("") != 0){
			m++;
			st = new StringTokenizer(line, " ");
			if(n == -1){
				n = st.countTokens();
			}else if (n != st.countTokens()){
				try{
					throw new InputException("The # of column should be " + n + ", but row #" + m + " has "+st.countTokens());
				}catch(InputException ie){
					System.out.println(ie.getMessage());
					return null;
				}
			}
			
			lineBuffer = new double[n];
			int i = 0;
			while(st.hasMoreTokens()){
				lineBuffer[i] = (Double.parseDouble(st.nextToken()));
				i++;
			}
			
			list.add(lineBuffer);
			
		}
		
		Matrix A = new Matrix(list, m, n);
		
		return A;
	}
	
	public double get(int i, int j){
		return matrix.get(i)[j];
	}
	public double[] getRow(int i){
		return matrix.get(i);
	}
	public double[] getColumn(int i){
		double[] column = new double[m];
		for(int j=0; j<m; j++){
			column[j] = matrix.get(j)[i];
		}
		return column;
	}
}
