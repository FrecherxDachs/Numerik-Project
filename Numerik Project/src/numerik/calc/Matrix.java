package numerik.calc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import numerik.ui.LatexFormula;
import numerik.ui.Recorder;


public class Matrix {

    private int     rows;
    private int     cols;
    public  String  name;                                        // Name der Matrix für Ausgabe
    
    LatexFormula formula  = new LatexFormula();
    Recorder     recorder = Recorder.getInstance();
    
    BigDecimal[][] values;

    // constructors
    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        values = new BigDecimal[rows][cols];

        for (int m = 0; m < rows; m++) {
            for (int n = 0; n < cols; n++) {
                values[m][n] = BigDecimal.ZERO;
            }
        }
    }
    
    
    public Matrix(BigDecimal[][] values) {
        this.rows = values.length;
        this.cols = values[0].length;

        this.values = values;
    }

    public Matrix(BigDecimal[] values, int cols) {
        this.cols = cols;
        this.rows = values.length / cols;

        this.values = new BigDecimal[rows][cols];

        int m = 0, n = 0;
        for (BigDecimal v : values) {
            this.values[m][n] = v;

            n++;
            if (n == cols) {
                n = 0;
                m++;
            }
        }
    }

    public Matrix(int cols, int rows, BigDecimal initValue) {
        this(cols, rows);

        for (int m = 0; m < values.length; m++) {
            for (int n = 0; n < values[m].length; n++) {
                values[m][n] = initValue;
            }
        }
    }


    public Matrix(String file, String name) {

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            boolean transmit = false;
            ArrayList<ArrayList<BigDecimal>> entries = new ArrayList<ArrayList<BigDecimal>>();
            while ((line = br.readLine()) != null) {
 
                if(line.contains("Matrix#"+name) || line.equals("") ) 
                    transmit = false;
                
                if (transmit) 
                {
                    ArrayList<BigDecimal> entry = new ArrayList<BigDecimal>();
                    entries.add(entry);
                
                    for (String number : line.split(",")) {
                        entry.add(new BigDecimal(number));
                    }
                }
                
                if(line.contains("Matrix#"+name)) 
                    transmit = true;
            }

            rows = entries.size();
            cols = entries.get(0).size();
            this.name = name;
            
            values = new BigDecimal[rows][cols];

            for (int n = 0; n < rows; n++) {
                for (int m = 0; m < cols; m++) {
                    values[n][m] = entries.get(n).get(m);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Matrix(ArrayList<BigDecimal> values, int cols)
    {
        this.cols = cols;
        this.rows = values.size() / cols;
        
        this.values = new BigDecimal[rows][cols];
        
        int m = 0, n = 0;
        for (BigDecimal v : values) {
            this.values[m][n] = v;
            
            n++;
            if (n == cols) {
                n = 0;
                m++;
            }
        }
    }
    
    // getters
    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
    
    protected void setRows(int rows) {
        this.rows = rows;
    }

    protected void setCols(int cols) {
        this.cols = cols;
    }

    public BigDecimal get(int row, int col) {
        if (!isValidIndex(row, col)) {
            System.err.println("Matrix.get-Funktion: Index out of Bounds: row="
                    + row + ", col=" + col);
            return BigDecimal.ZERO;
        }

        return values[row][col];
    }

    public void set(int row, int col, BigDecimal value) {
        if (!isValidIndex(row, col)) {
            System.err.println("Matrix.set-Funktion: Index out of Bounds: row="
                    + row + ", col=" + col);
        }

        values[row][col] = value;
    }

    
    // functions
    public boolean isQuadratic() {
        return rows == cols;
    }

    public Matrix identity() {
        // Einheitsmatrix muss quadratisch sein
        if (!isQuadratic()) {
            return null;
        }

        Matrix identity = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            identity.set(i, i, BigDecimal.ONE);
        }

        return identity;
    }

    public Matrix getTransposed() {
        Matrix transposedMatrix = new Matrix(cols, rows);

        for (int m = 0; m < rows; m++) {
            for (int n = 0; n < cols; n++) {
                transposedMatrix.set(n, m, values[m][n]);
            }
        }

        return transposedMatrix;
    }

    public Matrix add(Matrix x) {
        if (rows != x.getRows() || cols != x.getCols())
            return null;
        else {
            BigDecimal[][] v = new BigDecimal[rows][cols];
            for (int i = 0; i < values.length; i++) {
                for (int j = 0; j < values[i].length; j++) {
                    v[i][j] = values[i][j].add(x.get(i, j));
                }
            }
            return new Matrix(v);
        }
    }
    
    public Matrix mult(BigDecimal x) {
        BigDecimal[][] v = new BigDecimal[rows][cols];
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                v[i][j] = values[i][j].multiply(x);
            }
        }
        return new Matrix(v);
    }
    
    
    public Vector mult(Vector vector) {                       // Multipliziere Matrix und Vektor
        
        BigDecimal   sum = BigDecimal.ZERO;
        Vector newVector = new Vector( rows );
        
        for(int zeile=0; zeile<rows; zeile++) {
                for(int j=0; j<vector.getLength(); j++) {
                    sum = MathLib.round( sum.add( MathLib.round( values[zeile][j].multiply( vector.get(j) ))));
                }
                newVector.set(zeile, sum);
                sum = BigDecimal.ZERO;
        }
        
        newVector.name = vector.name;
        
        return newVector;
    }
    
    
    public Matrix mult(Matrix x) {
        if(cols != x.getRows())
            return null;
        else {
            BigDecimal[][] v = new BigDecimal[rows][x.getCols()];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < x.getCols(); j++) {
                    
                    BigDecimal sum = BigDecimal.ZERO;
                    
                    for (int j2 = 0; j2 < cols; j2++) {
                        sum = MathLib.round( sum.add( MathLib.round( values[i][j2].multiply( x.get(j2, j) ))));
                    }
                    v[i][j] = sum;
                }
            }
            
            return new Matrix(v);
        }
    }

    
    
    @Override
    public Matrix clone() {

        Matrix copy = new Matrix(rows, cols);
        copy.name = name;
        
        for(int row=0; row<rows; row++) {
            for(int col=0; col<cols; col++) {
                copy.values[row][col] = values[row][col];
            }
        }
        return copy;
    }

    
    
    // helper function
    private boolean isValidIndex(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    
    
    public Matrix getInverse() {
        
        Matrix inverse = new Matrix( rows, rows ).identity();
        
        if(!isQuadratic()) 
        {
            return null;
        } 
        else 
        {
            BigDecimal temp = BigDecimal.ZERO;
            Matrix    clone = clone();
            
            for(int col=0; col<rows-1; col++) 
            { // überführt A aus (A|E) in obere Dreiecksmatrix, Umformungen in A parallel in E
                for(int row=col; row<rows-1; row++) 
                {

                    temp = clone.values[row+1][col].divide( 
                           clone.values[col][col].negate(), MathLib.getInversePrecision(), RoundingMode.FLOOR );    
                    

                    for(int i=0; i<this.values.length; i++) 
                    {
                          clone.values[row+1][i] =   clone.values[row+1][i].add( temp.multiply(   clone.values[col][i] ));
                        inverse.values[row+1][i] = inverse.values[row+1][i].add( temp.multiply( inverse.values[col][i] ));
                    }
                    clone.values[row+1][col] = BigDecimal.ZERO;
                }
            }
            
            for(int row=0; row<rows; row++) 
            { // normiert Spur von A auf 1, Rechenschritte parallel auch in E durchführen
                temp = clone.values[row][row];
                for(int col=0; col<rows; col++) 
                {
                    inverse.values[row][col] = inverse.values[row][col].divide( temp, MathLib.getInversePrecision(), RoundingMode.HALF_UP );
                      clone.values[row][col] =   clone.values[row][col].divide( temp, MathLib.getInversePrecision(), RoundingMode.HALF_UP );
                }
            }
            
            for(int t=rows-1; t>=1; t--) 
            { // überführe A in Einheitsmatrix E, dann (A|E) -> (E|A^(-1))
                for(int row=rows-1; row>=0; row--) 
                {
                    if(row<t) 
                    {
                        temp = clone.values[row][t].negate();
                        for(int i=0; i<rows; i++) 
                        {
                            inverse.values[row][i] = inverse.values[row][i].add( temp.multiply( inverse.values[t][i] ));
                        }
                    }
                }
            }
        }
        return inverse;
    }
    

    public Vector solveX(Vector b) 
    {
        Vector clone_b = b.clone();
        
        recorder.setActive( false );
        Matrix L = getL( b.clone());
        
        recorder.setActive( true );
        Matrix U = getU( clone_b );
    
        Vector y = substitution( L, clone_b, "forward"  );
        Vector x = substitution( U, y,       "backward" );
        
        return x;
    }
    
    
    
    public Matrix getL() {
        return doLUDecomposition(0, null); // 0 liefert L zurück
    }
    
    
    
    public Matrix getU() {
        return doLUDecomposition(1, null); // 1 liefert U zurück
    }
    
    
    public Matrix getL(Vector b) {
        
        return doLUDecomposition( 0, b );
    }
    
    
    
    public Matrix getU(Vector b) {
        
        return doLUDecomposition( 1, b );
    }
    
    
    
    private Matrix doLUDecomposition(int which_matrix, Vector b) {
        
        BigDecimal temp = BigDecimal.ZERO;
        Matrix        U = clone();
        Matrix        L = identity();
             
        if(  name == null)   name = "A";
        if(b.name == null) b.name = "b";
        
        if (b!=null && recorder.isActive())
        {
            formula.addNewLine(2).addSolidLine().addNewLine(1);
            formula.addText("LU-Zerlegung").addNewLine(2);
        }
        
        for(int row=0; row<L.rows; row++) 
        { // Pivotisierung + Gaussschritte, reduzierte Zeilenstufenform
            if (MathLib.isPivotStrategy()) U = pivotColumnStrategy( U, b, row ); 
            
            for(int t=row; t<U.rows-1; t++) 
            {
                temp = MathLib.round( U.values[t+1][row].divide( U.values[row][row], MathLib.getInversePrecision(), RoundingMode.HALF_UP ));
                
                for(int i=row; i<U.rows; i++)
                { 
                    U.values[t+1][i] = MathLib.round( U.values[t+1][i].subtract( MathLib.round( temp.multiply( U.values[row][i] ) )));
                }  
                U.values[t+1][row] = temp;
                
                if (b!=null && recorder.isActive()) 
                    formula.addTildeText( name ).addText(" = ").addMatrix(U).addText(", ")
                           .addTildeText(b.name).addText(" = ").addVector(b).addNewLine(1);
            }
        }
        
        for(int row=0; row<L.rows; row++) 
        { // Trenne Matizen U und L voneinander
            for(int col=0; col<L.cols; col++) 
            {
                if (row>col) {
                    L.values[row][col] = U.values[row][col];
                    U.values[row][col] = BigDecimal.ZERO;
                }
            }   
        }
        
        if (b!=null && recorder.isActive()) 
        {  
            formula.addTildeText("L").addText(" = ").addMatrix(L).addNewLine(1);
            formula.addTildeText("U").addText(" = ").addMatrix(U).addNewLine(2);
            formula.addText("Probe: ").addTildeText("L").addSymbol("*").addTildeText("U")
                   .addText(" = ").addMatrix(L.mult(U)).addNewLine(2);
            formula.addSolidLine().addNewLine(1);
            recorder.add(formula);
        }
        
        if(which_matrix == 0) 
        {
            return L;
        } 
        else 
        {
            return U;
        }
    }
    
    
    
    public Vector substitution( Matrix matrix, Vector b, String str ) 
    {
        BigDecimal term0 = BigDecimal.ZERO;
        BigDecimal term1 = BigDecimal.ZERO;
        BigDecimal term2 = BigDecimal.ZERO;
        Vector         y = new Vector( b.getLength());
        
        if ( str.equals("forward")) 
        {
            y.set( 0, b.get(0) );
            
            for(int row=1; row<matrix.rows; row++) 
            {
                term0 = BigDecimal.ZERO;
                
                for(int i=0; i<y.getLength()-1; i++) 
                {
                    term0 = MathLib.round( matrix.values[row][i].multiply( y.get(i) )).add( term0 );
                }
                term1 = MathLib.round( b.get(row).subtract( term0 ));
                term2 = MathLib.round( term1.divide( matrix.values[row][row], MathLib.getInversePrecision(), RoundingMode.HALF_UP ));
                
                y.set( row, term2);
            }
            
            if (b!=null && recorder.isActive()) 
            {  
                formula.clear();
                formula.addText("Vorwärtssubstitution").addNewLine(2);
                formula.addText("y = ").addVector(y).addNewLine(2);
                formula.addSolidLine().addNewLine(2);
                recorder.add(formula);
            }
        }

        
        if ( str.equals("backward") ) 
        {
            int dim = matrix.getRows()-1;

            y.set(dim, MathLib.round( b.get(dim).divide( matrix.values[dim][dim], MathLib.getPrecision(), RoundingMode.HALF_UP )));

            for(int row=dim; row>=0; row--) 
            {
                term0 = BigDecimal.ZERO;
                for(int i=0; i<dim-row; i++) 
                {
                    term0 = MathLib.round( term0.add( MathLib.round( matrix.values[row][dim-i].multiply( y.get(dim-i) ))));
                }
                term1 = MathLib.round( b.get(row).subtract( term0 ));
                term2 = MathLib.round( term1.divide( matrix.values[row][row], MathLib.getPrecision(), RoundingMode.HALF_UP ));
                y.set( row, term2);
            }
        }
        
        return y;
    }
    
    
    
    public Matrix pivotColumnStrategy( Matrix matrix, Vector b, int row ) 
    {
        BigDecimal maximum = BigDecimal.ZERO;
        BigDecimal    temp = BigDecimal.ZERO;
        int    rowposition = 0;
        boolean    rowswap = false;
        
        for(int t=0; t<matrix.getRows()-row; t++) 
        {                              
            for(int i=row+t; i<matrix.getCols(); i++) 
            {                            
                if ( matrix.values[i][row].abs().compareTo( maximum ) == 1 ) 
                {  
                    rowposition = i;                                             // Markiere Zeile mit Maximum
                    maximum     = matrix.values[i][row].abs();
                    rowswap     = true;
                }
            }
            
            if (rowswap && rowposition!=row ) 
            {
                for(int col=0; col<matrix.getCols(); col++) 
                { // Zeilenvertauschung der Matrix   
                    temp                            = matrix.values[row][col];
                    matrix.values[row][col]         = matrix.values[rowposition][col];      
                    matrix.values[rowposition][col] = temp;
                }
                
                if(b!=null) 
                {
                    temp = b.get(row);                                               // Zeilenvertauschung des Vektors
                    b.set(row, b.get(rowposition));                             
                    b.set(rowposition, temp);
                }
                rowswap = false;
            }
        }
        return matrix;
    }
      
    
    public Matrix getScaleOf() 
    {
        Matrix scaledMatrix = identity();
        Vector        koeff = new Vector( rows );
        
        for(int row=0; row < rows; row++) 
        {
            for(int col=0; col<getCols(); col++) 
            {
                koeff.set(row, koeff.get(row).add( values[row][col].abs() ));
            }
            koeff.set(row, BigDecimal.ONE.divide( koeff.get(row), MathLib.getInversePrecision(), RoundingMode.HALF_UP ));
            
            scaledMatrix.values[row][row] = koeff.get(row).multiply( scaledMatrix.values[row][row] );
        }
        return scaledMatrix;
    }
    
    
    public BigDecimal det() {
        
        BigDecimal sum;
        
        if (!isQuadratic() || (rows>3 && cols>3)) 
        {
            return null;
        } 
        else 
        {
            // Determinanten mittels Sarrus Regel für 2x2 und 3x3 Matrizen
            int        dim = cols-1;
            BigDecimal det = BigDecimal.ONE;
                       sum = BigDecimal.ZERO;
                       
            for(int i=0; i<=dim; i++) 
            { // Regel von Sarrus: Teil 1 - Addition
                for(int t=0; t<=dim; t++)   
                {
                    if( i+t <= dim ) {
                        det = det.multiply( values[t][t+i] );
                    } else {
                        det = det.multiply( values[t][t+i-dim-1] );
                    }
                }
                sum = sum.add( det );
                det = BigDecimal.ONE;
            }
            
            
            for(int i=dim; i>=0; i--) 
            { // Regel von Sarrus: Teil 2 - Subtraktion
                for(int t=dim; t>=0; t--) 
                {
                    if( t+i-dim >= 0 ) {
                        det = det.multiply( values[dim-t][t+i-dim] );
                    } else {
                        det = det.multiply( values[dim-t][t+i+1] );
                    }
                sum = sum.subtract( det );
                det = BigDecimal.ONE;
                }
            }
        }
        return sum;
    }
    
    public Matrix jakobiMatrix(Vector vector) {
        
        Double[] x = vector.toDouble();  // x[0] = x_1 ; x[1] = x_2 ; usw.
        
        values[0][0] = BigDecimal.valueOf(   2*x[0]      );
        values[0][1] = BigDecimal.valueOf(   2*x[1]+0.6  );
        values[1][0] = BigDecimal.valueOf(   2*x[0]+1    );
        values[1][1] = BigDecimal.valueOf(  -2*x[1]-1.6  );
        
        return this;
    }
    
    public BigDecimal norm() {
        if( MathLib.getNorm()==0 ) return  zsnorm();
        if( MathLib.getNorm()==1 ) return fronorm();
        
        return null;
    }
    
    private BigDecimal zsnorm() 
    {              
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal max = BigDecimal.ZERO;
        
        for(int t=0; t<rows; t++) 
        {
            for(int i=0; i<rows; i++) 
            {
                sum = sum.add( values[t][i].abs() );
            }
            if ( max.compareTo( sum ) == -1 ) max = sum;
            
            sum = BigDecimal.ZERO;
        } 
        return MathLib.round( max );
    }
    
    private BigDecimal fronorm() {
        
        BigDecimal     sum = BigDecimal.ZERO;
        
        for(int row=0; row<rows; row++) 
        {
            for(int col=0; col<cols; col++) 
            {
                sum = sum.add( values[row][col].multiply( values[row][col] ) );
            }
        }
        return MathLib.sqrt( sum );
    }
    
    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[");
        for (int row = 0; row < rows; row++)
        {
            buffer.append("[");
            for (int col = 0; col < cols; col++)
            {
                buffer.append(values[row][col].toPlainString());
                if (col < cols - 1)
                {
                    buffer.append(",");
                }
            }
            buffer.append("]");
            if (row < rows - 1)
            {
                buffer.append(",");
            }
        }
        buffer.append("]");
        return buffer.toString();
    }
}