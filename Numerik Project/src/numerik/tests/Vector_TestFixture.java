package numerik.tests;

import numerik.calc.*;

import java.math.BigDecimal;
import org.junit.*;
import org.junit.rules.*;

import static org.junit.Assert.*;

public class Vector_TestFixture
{
    private Vector vectorInput;
    private Vector vectorInput2;
    
    private Matrix matrixOutput;
    private Vector vectorOutput;
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void add__Addiere_zwei_gleichdimensionale_Vektoren_miteinander()
    {
        vectorInput = new Vector(3);
        vectorInput2 = new Vector(3);
        
        vectorInput.set(0, new BigDecimal("9999995"));
        vectorInput.set(1, new BigDecimal("9999992"));
        vectorInput.set(2, new BigDecimal("0.0000"));
        
        vectorInput2.set(0, new BigDecimal("0.000001"));
        vectorInput2.set(1, new BigDecimal("9999995"));
        vectorInput2.set(2, new BigDecimal("0.000001111115"));
        
        vectorOutput = vectorInput.add(vectorInput2);
        
        assertEquals("10000000", vectorOutput.get(0).toPlainString());
        assertEquals("20000000", vectorOutput.get(1).toPlainString());
        assertEquals("0.00000111112", vectorOutput.get(2).toPlainString());
    }
    
    @Test
    public void add__Addiere_zwei_nichtgleichdimensionale_Vektoren_miteinander()
    {
        thrown.expect(ArithmeticException.class);
        thrown.expectMessage("Die Länge der beiden Vektoren muss übereinstimmen.");
        
        vectorInput = new Vector(3);
        vectorInput2 = new Vector(4);
        
        vectorOutput = vectorInput.add(vectorInput2);
    }
    
    @Test
    public void sub__Subtrahiere_zwei_gleichdimensionale_Vektoren_voneinander()
    {
        vectorInput = new Vector(3);
        vectorInput2 = new Vector(3);
        
        vectorInput.set(0, new BigDecimal("9999995"));
        vectorInput.set(1, new BigDecimal("9999992"));
        vectorInput.set(2, new BigDecimal("0.0000"));
        
        vectorInput2.set(0, new BigDecimal("0.000001"));
        vectorInput2.set(1, new BigDecimal("9999995"));
        vectorInput2.set(2, new BigDecimal("0.000001111115"));
        
        vectorOutput = vectorInput.sub(vectorInput2);
        
        assertEquals("10000000", vectorOutput.get(0).toPlainString());
        assertEquals("-10", vectorOutput.get(1).toPlainString());
        assertEquals("-0.00000111112", vectorOutput.get(2).toPlainString());
    }
    
    @Test
    public void mult__Multipliziere_eine_3x3_Matrix_mit_einem_Skalaren()
    {
        vectorInput = new Vector(3);
        
        vectorInput.set(0, new BigDecimal("4.332412341"));
        vectorInput.set(1, new BigDecimal("5.111112"));
        vectorInput.set(2, new BigDecimal("-9399999"));
        
        vectorOutput = vectorInput.mult(new BigDecimal("9999995"));
        
        assertEquals("43324100", vectorOutput.get(0).toPlainString());
        assertEquals("51111100", vectorOutput.get(1).toPlainString());
        assertEquals("-94000000000000", vectorOutput.get(2).toPlainString());
    }
    
    @Test
    public void divide__Dividiere_eine_3x3_Matrix_mit_einem_Skalaren()
    {
        vectorInput = new Vector(3);
        
        vectorInput.set(0, new BigDecimal("4.332412341"));
        vectorInput.set(1, new BigDecimal("5.111112"));
        vectorInput.set(2, new BigDecimal("-9399999"));
        
        vectorOutput = vectorInput.divide(new BigDecimal("0.00000009999995"));
        
        assertEquals("43324100", vectorOutput.get(0).toPlainString());
        assertEquals("51111100", vectorOutput.get(1).toPlainString());
        assertEquals("-94000000000000", vectorOutput.get(2).toPlainString());
    }
    
    @Test
    public void divide__Dividiere_eine_3x3_Matrix_mit_einem_Skalaren_Zero()
    {
        thrown.expect(ArithmeticException.class);
        thrown.expectMessage("Bei der Skalardivision kann nicht durch 0 geteilt werden.");
        
        vectorInput = new Vector(3);
        
        vectorInput.set(0, new BigDecimal("4.332412341"));
        vectorInput.set(1, new BigDecimal("5.111112"));
        vectorInput.set(2, new BigDecimal("-9399999"));
        
        vectorOutput = vectorInput.divide(new BigDecimal("0.00000000"));
    }
    
    @Test
    public void norm__Teste_Zeilensummennorm_eines_Vektors()
    {
        MathLib.setPrecision(30);
        MathLib.setNorm(0);
        
        Vector v1 = new Vector(3);
        
        v1.set(0, new BigDecimal("1000"));
        v1.set(1, new BigDecimal("10E+9"));
        v1.set(2, new BigDecimal("500000"));
        
        BigDecimal normValue = v1.norm();
        
        assertTrue(normValue.toPlainString().startsWith("10000000000"));
    }
    
    @Test
    public void norm__Teste_Euklidische_Norm_eines_Vektors()
    {
        MathLib.setPrecision(30);
        MathLib.setNorm(1);
        
        Vector v1 = new Vector(3);
        
        v1.set(0, new BigDecimal("1000"));
        v1.set(1, new BigDecimal("10E+9"));
        v1.set(2, new BigDecimal("500000"));
        
        BigDecimal normValue = v1.norm();
        
        assertTrue(normValue.toPlainString().startsWith("10000000012.5"));
    }
    
    @Test
    public void norm__Teste_Norm_Die_Noch_Nicht_Existiert()
    {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("MathLib.getNorm() liefert den Wert -1, für welche es keine Normimplementierung für Vektoren gibt.");
        
        MathLib.setNorm(-1);
        
        new Vector(3).norm();
    }
    
    // Beim vorherigen Klonen wurden die BigDecimals nicht geklont !!! gefährlich, deshalb wird ein neues BigDecimal mit dem Initialwert 0 erstellt und der Wert diesem hinzugefügt
    @Test
    public void clone__Teste_Cloning_Eines_Vektors()
    {
        vectorInput = new Vector(3);
        
        BigDecimal localBigDecimal1 = new BigDecimal("2");
        BigDecimal localBigDecimal2 = new BigDecimal("3");
        BigDecimal localBigDecimal3 = new BigDecimal("4");
        
        vectorInput.set(0, localBigDecimal1);
        vectorInput.set(1, localBigDecimal2);
        vectorInput.set(2, localBigDecimal3);
        
        vectorOutput = vectorInput.clone();
        
        assertEquals(3, vectorOutput.getLength());
        assertNotSame(vectorInput, vectorOutput);
        
        assertEquals("2", vectorInput.get(0).toPlainString());
        assertEquals("3", vectorInput.get(1).toPlainString());
        assertEquals("4", vectorInput.get(2).toPlainString());
        
        assertNotSame(localBigDecimal1, vectorOutput.get(0));
        assertNotSame(localBigDecimal2, vectorOutput.get(1));
        assertNotSame(localBigDecimal3, vectorOutput.get(2));
    }
    
    @Test
    public void toMatrix__Teste_Das_Umwandeln_Eines_Vektors_Untransponiert_In_Eine_Matrix()
    {
        Vector v1 = new Vector(new BigDecimal[]
            {
                new BigDecimal("1000"),
                new BigDecimal("10E+9"),
                new BigDecimal("500000")
            }, false);
        
        matrixOutput = v1.toMatrix();
        
        assertEquals(1, matrixOutput.getCols());
        assertEquals(3, matrixOutput.getRows());
        
        assertEquals("1000", matrixOutput.get(0, 0).toPlainString());
        assertEquals("10000000000", matrixOutput.get(1, 0).toPlainString());
        assertEquals("500000", matrixOutput.get(2, 0).toPlainString());
    }
    
    @Test
    public void toMatrix__Teste_Das_Umwandeln_Eines_Vektors_Transponiert_In_Eine_Matrix()
    {
        Vector v1 = new Vector(new BigDecimal[]
            {
                new BigDecimal("1000"),
                new BigDecimal("10E+9"),
                new BigDecimal("500000")
            }, true);
        
        matrixOutput = v1.toMatrix();
        
        assertEquals(1, matrixOutput.getRows());
        assertEquals(3, matrixOutput.getCols());
        
        assertEquals("1000", matrixOutput.get(0, 0).toPlainString());
        assertEquals("10000000000", matrixOutput.get(0, 1).toPlainString());
        assertEquals("500000", matrixOutput.get(0, 2).toPlainString());
    }
    
    /* Setzen von allgemeinen Werten, die bei jedem Test verwendet werden */
    @Before
    public void setUp()
    {
        MathLib.setPrecision(6);
        MathLib.enableRound(true);
    }
    
    /*
     * Zurücksetzen von Werten, um Ausgangszustand eines Tests zu erreichen -->
     * wichtig für weitere Tests
     */
    @After
    public void tearDown()
    {
        vectorInput = null;
        vectorInput2 = null;
        vectorOutput = null;
        
        matrixOutput = null;
        
        MathLib.enableRound(false);
    }
}
