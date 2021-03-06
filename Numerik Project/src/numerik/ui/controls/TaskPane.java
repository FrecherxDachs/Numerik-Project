
package numerik.ui.controls;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;

import numerik.expression.*;
import numerik.tasks.*;
import numerik.tasks.Argument.ArgType;
import numerik.ui.dialogs.*;
import numerik.ui.misc.*;
import numerik.ui.misc.MathDataSynchronizer.MathDataType;


public final class TaskPane extends JPanel implements ActionListener
{
    private static final MathDataSynchronizer DATA = MathDataSynchronizer.getInstance();
    
    private static final Icon RUN_ICON = new ImageIcon("icons/button_go_small.png");
    private static final Icon STOP_ICON = new ImageIcon("icons/stop-button.png");
    
    
    private JToolBar toolBar;
    private OutputFrame frame;
    private Task task;
    private Argument[] args;
    
    private Thread taskThread;
    
    private boolean initialized = false;
    
    
    public TaskPane(OutputFrame frame, Task task)
    {
        this.frame = frame;
        this.task = task;
        
        this.setLayout(new BorderLayout());
        
        // Der Task wird jetzt erst initialisiert, wenn er das erste Mal ausgewählt 
        // wird, um den Start der Anwendung zu beschleunigen
//        
//        task.init(frame, this);
    }
    
    
    public void createJToolBarByArguments(Argument... arguments)
    {
        toolBar = new JToolBar();
        toolBar.setLayout(new WrappingToolbarLayout(WrappingToolbarLayout.LEFT));
        toolBar.setFloatable(false);
        
        // Task ausführen, wenn Enter gedrückt wird
        frame.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "RUN");
        frame.getRootPane().getActionMap().put("RUN", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                runTask();
            }
        });
        
        JComboBox<String> combo;
        JTextField text;
        SyntaxTextArea expression;
        JCheckBox check;
        JSpinner spinner;
        SpinnerModel model;
        JButton button;
        JPanel pnlGroup = null;
        
        args = arguments;
        for (Argument arg : arguments)
        {
            if (arg.getName() != null && arg.getArgumentType() != ArgType.BOOLEAN)
            {
                pnlGroup = new JPanel();
                pnlGroup.add(new JLabel(arg.getName()));
            }
            
            switch (arg.getArgumentType())
            {
                case MATRIX:
                    combo = new MathDataComboBox(MathDataType.MATRIX, frame);
                    if (!arg.getDefaultValue().isEmpty())
                    {
                        combo.setSelectedItem(arg.getDefaultValue());
                    }
                    new ToolTippedComboBox(combo, DATA.getMatrixImages(), new Color(255, 255, 150));
                    DATA.addChangeListeners((MathDataComboBoxModel) combo.getModel());
                    combo.setPreferredSize(new Dimension(arg.getControlWidth(), combo.getPreferredSize().height));
                    arg.setRelatedControl(combo);
                    pnlGroup.add(combo);
                    toolBar.add(pnlGroup);
                    break;
                    
                case VECTOR:
                    combo = new MathDataComboBox(MathDataType.VECTOR, frame);
                    if (!arg.getDefaultValue().isEmpty())
                    {
                        combo.setSelectedItem(arg.getDefaultValue());
                    }
                    new ToolTippedComboBox(combo, DATA.getVectorImages(), new Color(255, 255, 150));
                    DATA.addChangeListeners((MathDataComboBoxModel) combo.getModel());
                    combo.setPreferredSize(new Dimension(arg.getControlWidth(), combo.getPreferredSize().height));
                    arg.setRelatedControl(combo);
                    pnlGroup.add(combo);
                    toolBar.add(pnlGroup);
                    break;
                    
                case DECIMAL:
                case DECIMAL_EX:
                case INTEGER:
                    text = new JTextField(arg.getDefaultValue());
                    text.setPreferredSize(new Dimension(arg.getControlWidth(), text.getPreferredSize().height));
                    text.setHorizontalAlignment(JTextField.RIGHT);
                    arg.setRelatedControl(text);
                    pnlGroup.add(text);
                    toolBar.add(pnlGroup);
                    break;
                    
                case EXPRESSION:
                    expression = new SyntaxTextArea(Arrays.asList(MathPool.FUNCTIONS), 
                            Arrays.asList(new String[] { "x", "y", "z" }), false);
                    expression.setPreferredSize(new Dimension(arg.getControlWidth(), expression.getPreferredSize().height));
                    expression.setText(arg.getDefaultValue());
                    arg.setRelatedControl(expression);
                    // Word-Wrap deaktivieren funktioniert leider noch nicht
                    JScrollPane scroll = new JScrollPane(expression, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    scroll.setBorder(null);
//                    pnlGroup.add(expression);
                    pnlGroup.add(scroll);
                    toolBar.add(pnlGroup);
                    break;
                    
                case BOOLEAN:
                    check = new JCheckBox(arg.getName() ,arg.getDefaultValue().equals("true"));
                    arg.setRelatedControl(check);
                    toolBar.add(check);
                    break;
                    
                case CHOICE:
                    combo = new JComboBox<String>(arg.getChoices());
                    arg.setRelatedControl(combo);
                    pnlGroup.add(combo);
                    toolBar.add(pnlGroup);
                    break;
                    
                case PRECISION:
                    model = new SpinnerNumberModel(Integer.parseInt(arg.getDefaultValue()), 1, 1000, 1);
                    spinner = new JSpinner(model);
                    spinner.setPreferredSize(new Dimension(arg.getControlWidth(), spinner.getPreferredSize().height));
                    arg.setRelatedControl(spinner);
                    pnlGroup.add(spinner);
                    toolBar.add(pnlGroup);
                    break;
                    
                case DOUBLEPRECISION:
                    model = new SpinnerNumberModel(Integer.parseInt(arg.getDefaultValue()), 1, 16, 1);
                    spinner = new JSpinner(model);
                    spinner.setPreferredSize(new Dimension(arg.getControlWidth(), spinner.getPreferredSize().height));
                    arg.setRelatedControl(spinner);
                    pnlGroup.add(spinner);
                    toolBar.add(pnlGroup);
                    break;
                    
                case RUN_BUTTON:
                    button = new JButton(RUN_ICON);
                    button.setActionCommand("RUN");
                    button.addActionListener(this);
                    toolBar.add(button);
                    break;
                    
                case STOP_BUTTON:
                    button = new JButton(STOP_ICON);
                    button.setActionCommand("STOP");
                    button.addActionListener(this);
                    toolBar.add(button);
            }
            
            toolBar.add(new JLabel(" "));
        }
    }
    
    
    public void setJToolBar(JToolBar toolBar)
    {
        this.toolBar = toolBar;
    }
    
    
    public void runTask()
    {
        killTask();
        
        taskThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    task.run(getParameters());
                }
                catch (IllegalArgumentException | ArithmeticException | IndexOutOfBoundsException ex)
                {
                    JLabel label = new JLabel(ex.getMessage());
                    label.setForeground(Color.RED);
                    setViewPortView(label);
                    
                    ex.printStackTrace();
                }
            }
        };
        
        taskThread.start();
    }
    
    
    @SuppressWarnings("deprecation")
    private void killTask()
    {
        if (taskThread != null && taskThread.isAlive())
        {
            taskThread.stop(); // nicht unbedingt schön, aber was solls
        }
    }
    
    
    public void showTask()
    {
        if (!initialized)
        {
            task.init(frame, this);
            initialized = true;
        }
        
        frame.setJToolBar(toolBar);
        runTask();
    }
    
    
    private Value[] getParameters() throws IllegalArgumentException
    {
        if(args == null)
        {
            return null;
        }
        
        ArrayList<Value> parameters = new ArrayList<Value>();
        
        for (Argument arg : args)
        {
            try
            {
                switch (arg.getArgumentType())
                {
                    case MATRIX:
                        parameters.add(new Value(DATA.getMatrix(((JComboBox<?>) arg.getRelatedControl()).getSelectedItem().toString())));
                        break;
                        
                    case VECTOR:
                        parameters.add(new Value(DATA.getVector(((JComboBox<?>) arg.getRelatedControl()).getSelectedItem().toString())));
                        break;
                        
                    case DECIMAL:
                        parameters.add(new Value(new BigDecimal(((JTextField) arg.getRelatedControl()).getText())));
                        break;
                        
                    case DECIMAL_EX:
                        parameters.add(new ExpressionEngine().solve(((JTextField) arg.getRelatedControl()).getText()));
                        break;
                        
                    case INTEGER:
                        parameters.add(new Value(new BigDecimal(Integer.parseInt(((JTextField) arg.getRelatedControl()).getText()))));
                        break;
                        
                    case EXPRESSION:
                        parameters.add(new Value(((SyntaxTextArea) arg.getRelatedControl()).getText()));
                        break;
                        
                    case BOOLEAN:
                        parameters.add(new Value(((JCheckBox) arg.getRelatedControl()).isSelected()));
                        break;
                        
                    case CHOICE:
                        parameters.add(new Value(((JComboBox<?>) arg.getRelatedControl()).getSelectedItem().toString()));
                        break;
                        
                    case PRECISION:
                    case DOUBLEPRECISION:
                        parameters.add(new Value(new BigDecimal((Integer)((JSpinner) arg.getRelatedControl()).getValue())));
                }
            }
            catch (NullPointerException ex)
            {
                throw new IllegalArgumentException("Überprüfen Sie die Eingabe von " + arg.getName());
            }
            catch (NumberFormatException ex)
            {
                throw new IllegalArgumentException(arg.getName() + " entspricht nicht der gewünschten Eingabe.");
            }
            catch (InvalidExpressionException ex)
            {
                throw new IllegalArgumentException("Überprüfen Sie die Eingabe von " + arg.getName());
            }
        }
        
        return parameters.toArray(new Value[parameters.size()]);
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch (e.getActionCommand())
        {
            case "RUN":
                runTask();
                break;
                
            case "STOP":
                killTask();
        }
    }
    
    
    public void setViewPortView(JComponent component)
    {
        this.removeAll();
        this.add(component);
        this.revalidate();
        this.repaint();
    }
}