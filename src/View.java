import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Hashtable;

public class View {

    private Model m;
    Model.MusicView mainMusicView = new Model.MusicView(1,4);
    JFrame frame = new JFrame("My Music Editor");
    Container contentPane = frame.getContentPane();
    JScrollPane scroll = new JScrollPane(mainMusicView);
    JPanel controlPanel = new JPanel();
    JLabel statusBar = new JLabel("<html><center>Showing Page 1 of 1 <br> Current Mode: Draw mode </center></html>",SwingConstants.CENTER);

    //Creating the Menu Bar
    JMenuBar mainMenu = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    JMenu editMenu = new JMenu("Edit");
    JMenuItem exit = new JMenuItem("Exit");
    JMenuItem addStaff = new JMenuItem("New Staff");
    JMenuItem deleteStaff = new JMenuItem("Delete Staff");
    JMenuItem newPageMenu = new JMenuItem("New Page");
    JMenuItem deletePageMenu = new JMenuItem("Delete Page");
    JMenuItem nextPageMenu = new JMenuItem("Next Page");
    JMenuItem prevPageMenu = new JMenuItem("Prev Page");

    //Creating the button panes and buttons for the control panel
    JPanel buttonPane1 = new JPanel();
    JButton select = new JButton("Select");
    JButton pen = new JButton("Pen");
    JPanel buttonPane2 = new JPanel();
    JButton newStaff = new JButton("New Staff");
    JButton deleteStaffButton = new JButton("Delete Staff");
    JPanel buttonPane3 = new JPanel();
    JButton play = new JButton("Play");
    JButton stop = new JButton("Stop");
    JPanel buttonPane4 = new JPanel();

    //Creating the Radio Buttons and Radio Button Group for the control panel
    JPanel radioButtonPane = new JPanel();
    JRadioButton note = new JRadioButton("Note");
    JRadioButton rest = new JRadioButton("Rest");
    JRadioButton flat = new JRadioButton("Flat");
    JRadioButton sharp = new JRadioButton("Sharp");
    ButtonGroup radioButtonGroup = new ButtonGroup();
    JPanel sliderPane = new JPanel();

    //Creating the Slider and its labels for the control panel
    JLabel whole = new JLabel("Whole");
    JLabel half = new JLabel("Half");
    JLabel quarter = new JLabel("Quarter");
    JLabel eighth = new JLabel("Eighth");
    JLabel sixteenth = new JLabel("Sixteenth");
    JSlider noteLength = new JSlider(JSlider.VERTICAL,0,40,0);
    Hashtable labels = new Hashtable();

    //Creating the button panes and their buttons for the control panel
    JPanel buttonPane5 = new JPanel();
    JButton newPage = new JButton("New Page");
    JButton deletePage = new JButton("Delete Page");
    JPanel navigationPanel = new JPanel();
    JButton nextPage = new JButton("Next Page");
    JButton prevPage = new JButton("Prev Page");


    public View()
    {
        //Setting the default close operation to close the frame when the close button is clicked
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //setting the layout for the screen
        contentPane.setLayout(new BorderLayout());

        //Setting scrollbar to be permanently visible on the screen
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        //Stylizing the status bar JLabel
        statusBar.setBorder(new LineBorder(Color.black,1));
        statusBar.setBorder(new EmptyBorder(15,0,15,0));
        statusBar.setBackground(Color.decode("#DEF2FF"));
        statusBar.setOpaque(true);
        statusBar.setFont(new Font("Dialog",Font.BOLD,15));

        //Adding menus and menu items to the menu bar and adding icons to menu items
        mainMenu.add(fileMenu);
        mainMenu.add(editMenu);
        addIconToMenu(exit,"/icons/exit_icon.png");
        exit.addActionListener(e->frame.dispose());
        fileMenu.add(exit);
        addIconToMenu(addStaff,"/icons/newstaff_icon.png");
        addIconToMenu(deleteStaff,"/icons/deletestaff_icon.png");
        addIconToMenu(newPageMenu,"/icons/newpage_icon.png");
        addIconToMenu(deletePageMenu,"/icons/deletepage_icon.png");
        addIconToMenu(nextPageMenu,"/icons/nextpage_icon.png");
        addIconToMenu(prevPageMenu,"/icons/prevpage_icon.png");
        editMenu.add(addStaff);
        editMenu.add(deleteStaff);
        editMenu.add(newPageMenu);
        editMenu.add(deletePageMenu);
        editMenu.add(nextPageMenu);
        editMenu.add(prevPageMenu);

        //Greys out the menu items initially
        deletePageMenu.setEnabled(false);
        nextPageMenu.setEnabled(false);
        prevPageMenu.setEnabled(false);

        //Adds menu bar to the frame
        frame.setJMenuBar(mainMenu);

        //Adds the different components to the content pane according to border layout specifications
        contentPane.add(scroll, BorderLayout.CENTER);
        contentPane.add(controlPanel, BorderLayout.WEST);
        contentPane.add(statusBar, BorderLayout.SOUTH);

        //Setting the layout for the control panel
        controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.Y_AXIS));
        controlPanel.setBorder(new EmptyBorder(10,20,10,20));

        //Setting the layout of button pane
        buttonPane1.setLayout(new FlowLayout());

        //Stylizing the buttons by setting size,font and adding icons before adding them to the button pane
        addIcon(select,"/icons/select_icon.png");
        addIcon(pen,"/icons/pen_icon.png");
        select.setPreferredSize(new Dimension(150,40));
        pen.setPreferredSize(new Dimension(150,40));
        select.setFont(new Font("SansSerif",Font.PLAIN,12));
        pen.setFont(new Font("SansSerif",Font.PLAIN,12));
        buttonPane1.add(select);
        buttonPane1.add(pen);
        controlPanel.add(buttonPane1);

        //Adding a Separator after button pane to group buttons
        controlPanel.add(new JSeparator());

        //Setting the layout of button pane
        buttonPane2.setLayout(new FlowLayout());

        //Stylizing the buttons by setting size,font and adding icons before adding them to the button pane
        addIcon(newStaff,"/icons/newstaff_icon.png");
        addIcon(deleteStaffButton,"/icons/deletestaff_icon.png");
        newStaff.setPreferredSize(new Dimension(150,40));
        deleteStaffButton.setPreferredSize(new Dimension(150,40));
        newStaff.setFont(new Font("SansSerif",Font.PLAIN,12));
        deleteStaffButton.setFont(new Font("SansSerif",Font.PLAIN,12));
        buttonPane2.add(newStaff);
        buttonPane2.add(deleteStaffButton);
        controlPanel.add(buttonPane2);

        //Adding a Separator after button pane to group buttons
        controlPanel.add(new JSeparator());

        //Setting the layout of button pane
        buttonPane3.setLayout(new FlowLayout());

        //Stylizing the buttons by setting size,font and adding icons before adding them to the button pane
        addIcon(play,"/icons/play_icon.png");
        addIcon(stop,"/icons/stop_icon.png");
        play.setPreferredSize(new Dimension(150,40));
        stop.setPreferredSize(new Dimension(150,40));
        play.setFont(new Font("SansSerif",Font.PLAIN,12));
        stop.setFont(new Font("SansSerif",Font.PLAIN,12));
        buttonPane3.add(play);
        buttonPane3.add(stop);
        controlPanel.add(buttonPane3);

        //Adding a Separator after button pane to group buttons
        controlPanel.add(new JSeparator());

        //Setting the layout of button pane
        buttonPane4.setLayout(new FlowLayout());

        //Setting the layout of radio button pane
        radioButtonPane.setLayout(new BoxLayout(radioButtonPane,BoxLayout.Y_AXIS));
        radioButtonPane.setBorder(new EmptyBorder(0,10,0,5));

        //Adding radio buttons to the radio button group
        radioButtonGroup.add(note);
        radioButtonGroup.add(rest);
        radioButtonGroup.add(flat);
        radioButtonGroup.add(sharp);

        //Setting Note as the default selected button
        note.setSelected(true);

        //Adding the radio buttons to the pane for visibility
        radioButtonPane.add(note);
        radioButtonPane.add(rest);
        radioButtonPane.add(flat);
        radioButtonPane.add(sharp);

        //Stylizing the radio button labels before adding radio button pane to the button pane
        note.setFocusPainted(false);
        rest.setFocusPainted(false);
        flat.setFocusPainted(false);
        sharp.setFocusPainted(false);
        note.setFont(new Font("SansSerif",Font.PLAIN,12));
        rest.setFont(new Font("SansSerif",Font.PLAIN,12));
        flat.setFont(new Font("SansSerif",Font.PLAIN,12));
        sharp.setFont(new Font("SansSerif",Font.PLAIN,12));
        buttonPane4.add(radioButtonPane);

        //Stylizing the slider pane and the slider's labels
        sliderPane.setBorder(new EmptyBorder(0,5,0,10));
        whole.setFont(new Font("SansSerif",Font.PLAIN,12));
        half.setFont(new Font("SansSerif",Font.PLAIN,12));
        quarter.setFont(new Font("SansSerif",Font.PLAIN,12));
        eighth.setFont(new Font("SansSerif",Font.PLAIN,12));
        sixteenth.setFont(new Font("SansSerif",Font.PLAIN,12));

        //Adding labels to the hashtable
        labels.put(0, whole);
        labels.put(10, half);
        labels.put(20, quarter);
        labels.put(30, eighth);
        labels.put(40, sixteenth);

        //Setting slider parameters before adding it to the slider pane
        noteLength.setMajorTickSpacing(10);
        noteLength.setMinorTickSpacing(0);
        noteLength.setPaintTicks(true);
        noteLength.setLabelTable(labels);
        noteLength.setPaintLabels(true);
        sliderPane.add(noteLength);
        buttonPane4.add(sliderPane);
        controlPanel.add(buttonPane4);

        //Setting the layout of button pane
        buttonPane5.setLayout(new FlowLayout());

        //Stylizing the buttons by setting size,font and adding icons before adding them to the button pane
        addIcon(newPage,"/icons/newpage_icon.png");
        addIcon(deletePage,"/icons/deletepage_icon.png");
        newPage.setPreferredSize(new Dimension(150,40));
        deletePage.setPreferredSize(new Dimension(150,40));
        deletePage.setEnabled(false);
        newPage.setFont(new Font("SansSerif",Font.PLAIN,12));
        deletePage.setFont(new Font("SansSerif",Font.PLAIN,12));
        buttonPane5.add(newPage);
        buttonPane5.add(deletePage);
        controlPanel.add(buttonPane5);

        //Setting the layout of navigation panel
        navigationPanel.setLayout(new FlowLayout());

        //Stylizing the buttons by setting size,font and adding icons before adding them to the navigation panel
        addIcon(nextPage,"/icons/nextpage_icon.png");
        addIcon(prevPage,"/icons/prevpage_icon.png");
        nextPage.setPreferredSize(new Dimension(150,40));
        prevPage.setPreferredSize(new Dimension(150,40));
        prevPage.setEnabled(false);
        nextPage.setEnabled(false);
        nextPage.setFont(new Font("SansSerif",Font.PLAIN,12));
        prevPage.setFont(new Font("SansSerif",Font.PLAIN,12));
        navigationPanel.add(prevPage);
        navigationPanel.add(nextPage);
        controlPanel.add(navigationPanel);

        //Displaying on the screen
        frame.pack();
        frame.setVisible(true);

    }

    //Function to add icons to buttons
    public void addIcon (JButton button, String path)
    {
        java.net.URL imgURL = getClass().getResource(path);
        ImageIcon icon=null;
        if(imgURL !=null)
        {
            icon = new ImageIcon(imgURL);
            Image img = icon.getImage();
            Image new_img;
            if(button==newPage||button==deletePage){
                new_img = img.getScaledInstance(25,25, Image.SCALE_SMOOTH);
            }
            else
                new_img = img.getScaledInstance(20,20, Image.SCALE_SMOOTH);

            Icon finalIcon = new ImageIcon(new_img);
            button.setIcon(finalIcon);
            button.setFocusPainted(false);
        }
    }

    //Function to add icons to menu items
    public void addIconToMenu (JMenuItem menuItem, String path)
    {
        java.net.URL imgURL = getClass().getResource(path);
        ImageIcon icon=null;
        if(imgURL !=null)
        {
            icon = new ImageIcon(imgURL);
            Image img = icon.getImage();
            Image new_img;
            if(menuItem==newPageMenu||menuItem==deletePageMenu){
                new_img = img.getScaledInstance(18,18, Image.SCALE_SMOOTH);
            }
            else
                new_img = img.getScaledInstance(15,15, Image.SCALE_SMOOTH);

            Icon finalIcon = new ImageIcon(new_img);
            menuItem.setIcon(finalIcon);
            menuItem.setFocusPainted(false);
        }
    }

    //Series of functions that return different components
    public JSlider getNoteLength()
    {
        return noteLength;
    }

    public JLabel getStatusBar()
    {
        return statusBar;
    }

    public JMenu getFileMenu()
    {
        return fileMenu;
    }

    public JMenu getEditMenu()
    {
        return editMenu;
    }

    public JMenuItem getAddStaff()
    {
        return addStaff;
    }

    public  JMenuItem getDeleteStaff()
    {
        return deleteStaff;
    }


    public JButton getSelect()
    {
        return select;
    }

    public JButton getPen()
    {
        return pen;
    }

    public JButton getPlay()
    {
        return play;
    }

    public JButton getStop()
    {
        return stop;
    }

    public JRadioButton getNote()
    {
        return note;
    }

    public JRadioButton getRest()
    {
        return rest;
    }

    public JRadioButton getFlat()
    {
        return flat;
    }

    public JRadioButton getSharp()
    {
        return sharp;
    }

    public JButton getNewStaff()
    {
        return newStaff;
    }

    public JButton getDeleteStaffButton()
    {
        return deleteStaffButton;
    }

    public JButton getNewPage()
    {
        return newPage;
    }

    public JButton getDeletePage()
    {
        return deletePage;
    }

    public JButton getNextPage()
    {
        return nextPage;
    }

    public JButton getPrevPage()
    {
        return prevPage;
    }

    public JMenuItem getNewPageMenu()
    {
        return newPageMenu;
    }

    public JMenuItem getDeletePageMenu()
    {
        return deletePageMenu;
    }

    public JMenuItem getNextPageMenu()
    {
        return nextPageMenu;
    }

    public JMenuItem getPrevPageMenu()
    {
        return prevPageMenu;
    }

    public Model.MusicView getMusicView()
    {
        return mainMusicView;
    }

    public void setMainMusicView(Model.MusicView m) {
        this.mainMusicView=m;
    }

    public JScrollPane getScroll() {return scroll;}

    public JPanel getControlPanel() {return controlPanel;}

    public JFrame getFrame() {return frame;}

}
