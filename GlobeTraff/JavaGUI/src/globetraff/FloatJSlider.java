//package uk.ac.starlink.splat.iface;
package globetraff;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.text.DecimalFormat;

import java.lang.reflect.Field;
import javax.swing.SwingUtilities;

//import uk.ac.starlink.ast.gui.DecimalField;
//import uk.ac.starlink.ast.gui.ScientificFormat;


/**
 * Creates a compound component that displays a floating point text
 * entry field and a coupled slider. The text field is optional so
 * this component may also be just used as a floating point slider.
 * <p>
 * The major advantage of this component over a simple JSlider is that
 * the apparent representation of the values is as a range of floating
 * point values. The precision of the value is initially determined by
 * setting the number of steps between the slider limits.
 *
 * @author Peter W. Draper
 * @since $Date$
 * @since 17-OCT-2000
 * @version $Id$
 */
public class FloatJSlider extends JPanel
{
    /**
     * Reference to text field showing the current value.
     */
    protected DecimalField valueField;

    /**
     * The slider widget.
     */
    protected JSlider slider;

    /**
     * Whether the text field is on show.
     */
    protected boolean showTextField = true;

    /**
     * Slider model that supports floating point.
     */
    private FloatJSliderModel model;

    /**
     *  Create an instance of FloatJSlider with text entry field.
     */
    FloatJSlider( FloatJSliderModel model ) {
        this( model, true, 0, false);
    }

    /**
     *  Create an instance of FloatJSlider, with optional text entry field.
     */
    FloatJSlider( FloatJSliderModel model, boolean showTextField, int rangeType, boolean vertical )
    {
        this.model = model;
        this.showTextField = showTextField;

        //  Use a BoxLayout to arrange the widgets in a row/column.
        setLayout( new BoxLayout( this, BoxLayout.PAGE_AXIS) );

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(globetraff.GlobeTraffApp.class).getContext().getResourceMap(FloatJSlider.class);
        
        java.awt.Font font = resourceMap.getFont("float_slider.font");

        if ( showTextField ) {
            // Add the text field.  It initially displays "0" and needs
            // to be at least 10 columns wide.
            //ScientificFormat scientificFormat = new ScientificFormat();
            valueField = new DecimalField( 0, 10, new DecimalFormat()/*scientificFormat*/ );
            valueField.setFont(font);
            valueField.setDoubleValue( model.getDoubleValue() );
            valueField.setMaximumSize(new Dimension(40,30));
            valueField.setBorder(null);

            valueField.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        syncModel();
                        fireStateChanged( new ChangeEvent( this ) );
                    }
                });
        }

        // Add the slider.
        slider = new JSlider( model );
        slider.setFont(resourceMap.getFont("float_slider.font"));

        model.addChangeListener( new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                syncText();
                fireStateChanged( e );
            }
        });

        slider.setLabelTable(prepareLabelTable(font, rangeType));
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(model.range/4);
        slider.setMinorTickSpacing(model.range/20);
        slider.setPaintTicks(true);
        
        if (vertical)
            slider.setOrientation(JSlider.VERTICAL);
        
        //Hiding current slider value, using the textfield instead
        //which is also used for input
        try
        {
        Class<?> sliderUIClass = Class.forName("javax.swing.plaf.synth.SynthSliderUI");
        final Field paintValue = sliderUIClass.getDeclaredField("paintValue");
        paintValue.setAccessible(true);

             try {
                        paintValue.set(slider.getUI(), false);
                 }
             catch (Exception e) {
                        throw new RuntimeException(e);
                    }
        } catch (Exception e) {};

        // Put them in place.
        if ( showTextField ) {
            add( valueField );
        }
        add( slider );
    }

    /**
     *  Prepare the labels for the slider
     */
    protected java.util.Hashtable prepareLabelTable(java.awt.Font font, int rangeType)
    {
        java.util.Hashtable labelTable = new java.util.Hashtable();
        javax.swing.JLabel fifth;
        javax.swing.JLabel fourth;
        javax.swing.JLabel third;
        javax.swing.JLabel second;
        javax.swing.JLabel first;

        double interval; 

        if (model.apparentMinimum == 0)
            interval = model.apparentMaximum/4;
        else if (model.apparentMinimum < 0)
            interval = model.apparentMaximum/2;
        else
            interval = (model.apparentMaximum-model.apparentMinimum)/4;

        Double five = new Double(model.apparentMaximum);
        Double four = new Double(five - interval);
        Double three = new Double(four - interval);
        Double two = new Double(three - interval);
        Double one = new Double(model.apparentMinimum);

        if ((model.resolution >=1) || (rangeType == GlobeTraffView.ED))
        {
            fifth = new javax.swing.JLabel(Integer.toString(five.intValue()));
            fourth = new javax.swing.JLabel(Integer.toString(four.intValue()));
            third = new javax.swing.JLabel(Integer.toString(three.intValue()));
            second = new javax.swing.JLabel(Integer.toString(two.intValue()));
            first = new javax.swing.JLabel(Integer.toString(one.intValue()));
        }
        else
        {
            fifth = new javax.swing.JLabel(five.toString());
            fourth = new javax.swing.JLabel(four.toString());
            third = new javax.swing.JLabel(three.toString());
            second = new javax.swing.JLabel(two.toString());
            first = new javax.swing.JLabel(one.toString());
        }

        //System.out.println("Resolution = "+model.resolution+" >=1"+"five="+five+" five.shortValue()"+five.shortValue()+" fifth ="+fifth.getText());
        //System.out.println("\t"+"Range= "+model.range+" RealMax="+model.realMaximum+" RealMin="+model.realMinimum+" ApparentMax="+model.apparentMaximum+" ApparentMinimum="+model.apparentMinimum+" Resolution = "+model.resolution);

        fifth.setFont(font);
        fourth.setFont(font);
        third.setFont(font);
        second.setFont(font);
        first.setFont(font);

        labelTable.put(new Integer(model.range), fifth);
        labelTable.put(new Integer((int)3*model.range/4), fourth);
        labelTable.put(new Integer((int)model.range/2), third);
        labelTable.put(new Integer((int)model.range/4), second);

        switch (rangeType)
        {
            case GlobeTraffView.ED:
                    labelTable.put(new Integer(1), first);
                    break;
            default: //(Mandelbrot) Zipf Slope, Popularity Bias
                    labelTable.put(new Integer(0), first);
                    break;
        }

        return labelTable;
    }

    /**
     *  Syncronise the model to the text field value.
     */
    protected void syncModel()
    {
        model.setDoubleValue( valueField.getDoubleValue() );
    }

    /**
     *  Syncronise the text field to the model (slider) value.
     */
    protected void syncText()
    {
        if ( showTextField ) {
            valueField.setDoubleValue( model.getDoubleValue() );
        }
    }

    /**
     * Returns the current values
     */
    public double getValue() {
        return model.getDoubleValue();
    }

    /**
     * Set the tooltip for the text and slider.
     */
    public void setToolTipText( String tip ) {
        if ( showTextField ) {
            valueField.setToolTipText( tip );
        }
        slider.setToolTipText( tip );
        super.setToolTipText( tip );
    }

    /**
     * Enable/Disable both components.
     */
    public void setEnabled(boolean val)
    {
        slider.setEnabled( val );
        if ( showTextField ) {
            valueField.setEnabled( val );
        }
    }

    /**
     * Disable both components.
     */
/*    public void disable()
    {
        slider.setEnabled( false );
        if ( showTextField ) {
            valueField.setEnabled( false );
        }
    }
 * 
 */

    public void reset()
    {
        model.reset();
    }


//
//  ChangeListener interface proxy to that of the Slider.
//
    protected EventListenerList listeners = new EventListenerList();

    /**
     * Adds a ChangeListener.
     */
    public void addChangeListener( ChangeListener l ) {
        listeners.add( ChangeListener.class, l );
    }

    /**
     * Forward a ChangeEvent from the slider.
     */
    protected void fireStateChanged( ChangeEvent e ) {
        Object[] la = listeners.getListenerList();
        for (int i = la.length - 2; i >= 0; i -= 2) {
            if (la[i]==ChangeListener.class) {
                ((ChangeListener)la[i+1]).stateChanged( e );
            }
        }
    }

    /**
     *  Test method.
     */
    public static void main( String args[] ) {

        //FloatJSliderModel( 1.0, 1.0, 100.0, 0.01 );
        FloatJSliderModel model = new FloatJSliderModel( 0.5, 0.0, 1.0, 0.01 );
        FloatJSlider fslider = new FloatJSlider( model, true,0, false );
        //jPanel2.add(jSlider7, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, 190, 72));

        //  Create a frame to hold graphics.
        JFrame frame = new JFrame();
        frame.getContentPane().add( fslider );

        //  Make all components of window decide their sizes.
        frame.pack();

        //  Center the window on the screen.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if ( frameSize.height > screenSize.height ) {
            frameSize.height = screenSize.height;
        }
        if ( frameSize.width > screenSize.width ) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation( ( screenSize.width - frameSize.width ) / 2,
                           ( screenSize.height - frameSize.height ) / 2 );

        //  Make interface visible.
        frame.setVisible( true );

        //  Application exits when this window is closed.
        frame.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent evt ) {
                System.exit( 1 );
            }
        });
    }
}