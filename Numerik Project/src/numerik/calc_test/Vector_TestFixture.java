package numerik.calc_test;

import numerik.calc.*;

import java.math.BigDecimal;
import org.junit.*;

import static org.junit.Assert.*;
import static numerik.calc.Vector.*;

public class Vector_TestFixture
{
    private Vector vectorInput;
    private Matrix matrixInput;
    
    private Matrix matrixOutput;
    private Vector vectorOutput;
    
    /* Setzen von allgemeinen Werten, die bei jedem Test verwendet werden */
    @Before
    public void setUp()
    {
        MathLib.setPrecision(6);
        MathLib.enableRound(true);
    }
    
    /*
     * Zur�cksetzen von Werten, um Ausgangszustand eines Tests zu erreichen -->
     * wichtig f�r weitere Tests
     */
    @After
    public void tearDown()
    {
        vectorInput = null;
        matrixInput = null;
        
        matrixOutput = null;
        
        MathLib.enableRound(false);
    }
}
