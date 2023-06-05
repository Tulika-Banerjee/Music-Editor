import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.*;

public class Controller {

    private Model model;
    private View view;
    //variables to keep track of system state
    private String currentDuration ="Whole";
    private String currentRadio = "Note";
    private int[] currentNote;
    private int[] currentRest;
    private int[] pos;
    private String currentMode = "Draw";

    private int noteY;
    private int restY;
    private int currVal = 0;
    //Controller constructor that takes the model and views as input
    public Controller(Model m, View v)
    {
        model = m;
        view = v;
    }
    //Setter methods for private variables defined above
    public void setNoteY(int y)
    {
        this.noteY=y;
    }
    public void setRestY(int y)
    {
        this.restY=y;
    }
    public void setCurrVal(int val)
    {
        this.currVal=val;
    }
    public void setCurrentRadio(String radio)
    {
        this.currentRadio=radio;
    }

    public void setCurrentDuration(String duration)
    {
        this.currentDuration=duration;
    }

    private void setCurrentNote(int[] pos)
    {
        this.currentNote=pos;
    }

    private void setCurrentRest(int[] pos)
    {
        this.currentRest=pos;
    }

    private void setCurrentMode(String mode)
    {
        this.currentMode=mode;
    }

    private void setPos(int[] pos)
    {
        this.pos=pos;
    }

    //Adding Listeners dynamically for each MusicView object
    public void addListeners(Model.MusicView mv) {
        mv.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                Point point = e.getPoint();
                //Adds new note/rest to screen at mouse cursor position
                if(currentMode=="Draw") {
                    if(currentRadio=="Note") {
                        int[] pos = mv.addNote(currentDuration, x, y);
                        {
                            if (pos != null) {
                                setCurrentNote(pos);
                            }
                        }
                    }
                    if(currentRadio=="Rest") {int[] pos = mv.addRest(currentDuration,x,y);
                        if(pos!=null) setCurrentRest(pos);}
                }
                //Makes selection based on point coordinates
                if(currentMode=="Select") {
                    int[] pos=mv.selectNoteRest(point);
                    setPos(pos);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if(currentMode=="Draw")
                {
                    //Calls vertical note snapping and displays the pitch
                    if(currentRadio=="Note"&&currentNote!=null)
                    {   //Changes associated staff for an edited note/rest if applicable
                        int[] send_pos = {0,currentNote[0],currentNote[1]};
                        int[] new_pos =mv.setNoteRest(send_pos);
                        int[] curr = {new_pos[1],new_pos[2]};
                        setCurrentNote(curr);
                        mv.verticalNoteSnapping(currentDuration,currentNote);
                        String pitch = mv.getPitch(currentNote);
                        if(pitch!=null)
                            view.getStatusBar().setText("<html><center>The pitch of the given note is: " + pitch +"<br> Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");
                        else view.getStatusBar().setText("<html><center>Note is outside the staff<br> Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");
                        setCurrentNote(null);}
                    //Calls vertical rest snapping
                    if(currentRadio=="Rest"&&currentRest!=null) {
                        mv.verticalRestSnapping(currentDuration,currentRest);
                        setCurrentRest(null);
                    }
                }
                if(currentMode=="Select"&&pos!=null)
                {
                    //Changes associated staff for an edited note/rest if applicable
                    setPos(mv.setNoteRest(pos));
                    //Calls vertical snapping and displays the pitch
                    if(pos[0]==0)
                    {
                        int[] new_pos = {pos[1],pos[2]};
                        mv.verticalNoteSnappingEdit(new_pos);
                        String pitch = mv.getPitch(new_pos);
                        if(pitch!=null)
                            view.getStatusBar().setText("<html><center>The pitch of the given note is: " + pitch +"<br> Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");
                        else view.getStatusBar().setText("<html><center>Note is outside the staff<br> Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");
                    }
                    //Calls vertical rest snapping
                    if(pos[0]==1)
                    {
                        int[] new_pos = {pos[1],pos[2]};
                        mv.verticalRestSnappingEdit(new_pos);
                    }
                }

                mv.requestFocusInWindow();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        mv.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                //Updates position of newly added note or rest when dragged in Draw mode
                if(currentMode=="Draw")
                {
                    if(currentRadio=="Note"&&currentNote!=null) mv.updateNote(currentNote,x,y);

                    if(currentRadio=="Rest"&&currentRest!=null) mv.updateRest(currentRest,x,y);
                }
                //Updates position of selected pre-existing note or rest when dragged in Select mode
                if(currentMode=="Select"&&pos!=null)
                {
                    mv.editNoteRest(pos,x,y);
                }
                mv.requestFocusInWindow();

            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });

        mv.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                //Deletes the selected note or rest upon pressing the delete key or backspace key
                if((e.getKeyCode()==KeyEvent.VK_DELETE||e.getKeyCode()==KeyEvent.VK_BACK_SPACE)&&currentMode=="Select"&&pos!=null) {
                    mv.deleteNoteRest(pos);
                    setPos(null);
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    public void initController()
    {
        //Instantiates and Adds first MusicView object to list of pages and scrollPane
        Model.MusicView mv = new Model.MusicView(1,4);
        addListeners(mv);
        view.getScroll().getViewport().add(mv);
        view.setMainMusicView(mv);
        model.addPage(mv);

        //Menu listener functionality specified for File Menu
       view.getFileMenu().addMenuListener(new MenuListener() {
           @Override
           public void menuSelected(MenuEvent e) {
               view.getStatusBar().setText("<html><center>File Menu currently selected <br> Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");
           }

           @Override
           public void menuDeselected(MenuEvent e) {

           }

           @Override
           public void menuCanceled(MenuEvent e) {

           }
       });

       //Menu listener functionality specified for Edit Menu
       view.getEditMenu().addMenuListener(new MenuListener() {
           @Override
           public void menuSelected(MenuEvent e) {
               view.getStatusBar().setText("<html><center>Edit Menu currently selected <br> Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");
           }

           @Override
           public void menuDeselected(MenuEvent e) {

           }

           @Override
           public void menuCanceled(MenuEvent e) {

           }
       });

       //Button press functionality specified for buttons and radio buttons

       view.getSelect().addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               //Defining toggle functionality for Select button
               if(currentMode=="Select") {
                   setCurrentMode("Draw");
                   view.getMusicView().resetSelect();
                   setPos(null);
               }
               else setCurrentMode("Select");
               view.getStatusBar().setText("<html><center>Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");
           }
       });
       view.getPen().addActionListener(e->view.getStatusBar().setText("<html><center>Pen Button currently selected<br>Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>"));
       view.getPlay().addActionListener(e->view.getStatusBar().setText("<html><center>Play Button currently selected<br>Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>"));
       view.getStop().addActionListener(e->view.getStatusBar().setText("<html><center>Stop Button currently selected<br>Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>"));

       view.getNote().addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               view.getStatusBar().setText("<html><center>Note Radio Button currently selected<br>Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");
               setCurrentRadio("Note");
           }
       });

        view.getRest().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getStatusBar().setText("<html><center>Rest Radio Button currently selected<br>Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");
                setCurrentRadio("Rest");
            }
        });
       view.getFlat().addActionListener(e->view.getStatusBar().setText("<html><center>Flat Radio Button currently selected<br>Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>"));
       view.getSharp().addActionListener(e->view.getStatusBar().setText("<html><center>Sharp Radio Button currently selected<br>Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>"));

       //New Page functionality specified on button click
       view.getNewPage().addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {

               view.getMusicView().resetSelect();
               Model.MusicView newPage = new Model.MusicView(model.getNumberOfPages()+1,4);
               model.addPage(newPage);
               int numberOfPages = model.getNumberOfPages();
               model.setCurrentPage(numberOfPages);
               view.getNextPage().setEnabled(false);
               view.getNextPageMenu().setEnabled(false);
               view.getScroll().getViewport().remove(view.getMusicView());
               view.getScroll().getViewport().add(newPage);
               addListeners(newPage);
               setPos(null);
               setCurrentNote(null);
               setCurrentRest(null);
               view.setMainMusicView(newPage);

               view.getStatusBar().setText("<html><center>Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");

               if(!view.getDeletePage().isEnabled() && numberOfPages>1) {
                   view.getDeletePage().setEnabled(true);
                   view.getDeletePageMenu().setEnabled(true);
               }
               if(!view.getPrevPage().isEnabled())
                   view.getPrevPage().setEnabled(true);
               int currentPage=model.getCurrentPage();
               int staffCount = model.getStaffCount(currentPage);

               if(staffCount==1)
               {
                   view.getDeleteStaff().setEnabled(false);
                   view.getDeleteStaffButton().setEnabled(false);
               }
               if(!view.getDeleteStaff().isEnabled() && staffCount>1) {
                   view.getDeleteStaff().setEnabled(true);
                   view.getDeleteStaffButton().setEnabled(true);
               }
           }
       });

        //Delete Page functionality specified on button click
        view.getDeletePage().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int currentPage=model.getCurrentPage();

                view.getScroll().getViewport().remove(view.getMusicView());
                model.deletePage(currentPage);
                int numberOfPages = model.getNumberOfPages();
                if(currentPage>numberOfPages) {
                    model.prevPage();
                    currentPage=model.getCurrentPage();
                }

                setPos(null);
                setCurrentNote(null);
                setCurrentRest(null);
                view.getScroll().getViewport().add(model.getPage(currentPage));
                view.setMainMusicView(model.getPage(currentPage));

                view.getStatusBar().setText("<html><center>Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");

                if(numberOfPages==1) {
                    view.getDeletePage().setEnabled(false);
                    view.getDeletePageMenu().setEnabled(false);
                    view.getNextPage().setEnabled(false);
                    view.getPrevPage().setEnabled(false);
                }
                if(currentPage==numberOfPages)
                {
                    view.getNextPage().setEnabled(false);
                }
                int staffCount = model.getStaffCount(currentPage);
                if(staffCount==1)
                {
                    view.getDeleteStaff().setEnabled(false);
                    view.getDeleteStaffButton().setEnabled(false);
                }
                if(!view.getDeleteStaff().isEnabled() && staffCount>1) {
                    view.getDeleteStaff().setEnabled(true);
                    view.getDeleteStaffButton().setEnabled(true);
                }
            }
        });

        //New Page Menu functionality specified on menu item selection/button click
        view.getNewPageMenu().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                view.getMusicView().resetSelect();
                Model.MusicView newPage = new Model.MusicView(model.getNumberOfPages()+1,4);
                model.addPage(newPage);
                int numberOfPages = model.getNumberOfPages();
                model.setCurrentPage(numberOfPages);
                view.getNextPage().setEnabled(false);
                view.getNextPageMenu().setEnabled(false);
                view.getScroll().getViewport().remove(view.getMusicView());
                view.getScroll().getViewport().add(newPage);
                addListeners(newPage);
                setPos(null);
                setCurrentNote(null);
                setCurrentRest(null);
                view.setMainMusicView(newPage);

                view.getStatusBar().setText("<html><center>Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");

                if(!view.getDeletePage().isEnabled() && numberOfPages>1) {
                    view.getDeletePage().setEnabled(true);
                    view.getDeletePageMenu().setEnabled(true);
                }
                if(!view.getPrevPage().isEnabled()) {
                    view.getPrevPage().setEnabled(true);
                    view.getPrevPageMenu().setEnabled(true);
                }
                int currentPage=model.getCurrentPage();
                int staffCount = model.getStaffCount(currentPage);

                if(staffCount==1)
                {
                    view.getDeleteStaff().setEnabled(false);
                    view.getDeleteStaffButton().setEnabled(false);
                }
                if(!view.getDeleteStaff().isEnabled() && staffCount>1) {
                    view.getDeleteStaff().setEnabled(true);
                    view.getDeleteStaffButton().setEnabled(true);
                }
            }
        });

        //Delete Page Menu functionality specified on menu item selection/button click
        view.getDeletePageMenu().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getStatusBar().setText(" Delete Page Menu Item currently selected");
                int currentPage=model.getCurrentPage();

                view.getScroll().getViewport().remove(view.getMusicView());
                model.deletePage(currentPage);
                int numberOfPages = model.getNumberOfPages();

                if(currentPage>numberOfPages) {
                    model.prevPage();
                    currentPage=model.getCurrentPage();
                }

                setPos(null);
                setCurrentNote(null);
                setCurrentRest(null);
                view.getScroll().getViewport().add(model.getPage(currentPage));
                view.setMainMusicView(model.getPage(currentPage));

                view.getStatusBar().setText("<html><center>Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");

                if(numberOfPages==1) {
                    view.getDeletePage().setEnabled(false);
                    view.getDeletePageMenu().setEnabled(false);
                    view.getNextPage().setEnabled(false);
                    view.getPrevPage().setEnabled(false);
                    view.getNextPageMenu().setEnabled(false);
                    view.getPrevPageMenu().setEnabled(false);
                }
                if(currentPage==numberOfPages)
                {
                    view.getNextPage().setEnabled(false);
                    view.getNextPageMenu().setEnabled(false);
                }
                int staffCount = model.getStaffCount(currentPage);
                if(staffCount==1)
                {
                    view.getDeleteStaff().setEnabled(false);
                    view.getDeleteStaffButton().setEnabled(false);
                }
                if(!view.getDeleteStaff().isEnabled() && staffCount>1) {
                    view.getDeleteStaff().setEnabled(true);
                    view.getDeleteStaffButton().setEnabled(true);
                }
            }
        });

        //Next Page functionality specified on button click
        view.getNextPage().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getMusicView().resetSelect();
                model.nextPage();
                int currentPage = model.getCurrentPage();
                int numberOfPages = model.getNumberOfPages();
                view.getScroll().getViewport().remove(view.getMusicView());
                view.getScroll().getViewport().add(model.getPage(currentPage));
                view.setMainMusicView(model.getPage(currentPage));
                setPos(null);
                setCurrentNote(null);
                setCurrentRest(null);

                view.getStatusBar().setText("<html><center>Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");

                if(currentPage==numberOfPages)
                {
                    view.getNextPage().setEnabled(false);
                    view.getNextPageMenu().setEnabled(false);
                }
                if(!view.getPrevPage().isEnabled() && currentPage!=1)
                {
                    view.getPrevPage().setEnabled(true);
                    view.getPrevPageMenu().setEnabled(true);
                }
                int staffCount = model.getStaffCount(currentPage);
                if(staffCount==1)
                {
                    view.getDeleteStaff().setEnabled(false);
                    view.getDeleteStaffButton().setEnabled(false);
                }
                if(!view.getDeleteStaff().isEnabled() && staffCount>1) {
                    view.getDeleteStaff().setEnabled(true);
                    view.getDeleteStaffButton().setEnabled(true);
                }

            }
        });

        //Previous Page functionality specified on button click
        view.getPrevPage().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getMusicView().resetSelect();
                model.prevPage();
                int currentPage = model.getCurrentPage();
                int numberOfPages = model.getNumberOfPages();
                view.getScroll().getViewport().remove(view.getMusicView());
                view.getScroll().getViewport().add(model.getPage(currentPage));
                view.setMainMusicView(model.getPage(currentPage));
                setPos(null);
                setCurrentNote(null);
                setCurrentRest(null);

                view.getStatusBar().setText("<html><center>Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");

                if(currentPage==1)
                {
                    view.getPrevPage().setEnabled(false);
                    view.getPrevPageMenu().setEnabled(false);
                }
                if(!view.getNextPage().isEnabled()&&currentPage!=numberOfPages)
                {
                    view.getNextPage().setEnabled(true);
                    view.getNextPageMenu().setEnabled(true);
                }
                int staffCount = model.getStaffCount(currentPage);
                if(staffCount==1)
                {
                    view.getDeleteStaff().setEnabled(false);
                    view.getDeleteStaffButton().setEnabled(false);
                }
                if(!view.getDeleteStaff().isEnabled() && staffCount>1) {
                    view.getDeleteStaff().setEnabled(true);
                    view.getDeleteStaffButton().setEnabled(true);
                }

            }
        });

        //Next Page Menu functionality specified on menu item selection/button click
        view.getNextPageMenu().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getMusicView().resetSelect();
                model.nextPage();
                int currentPage = model.getCurrentPage();
                int numberOfPages = model.getNumberOfPages();
                view.getScroll().getViewport().remove(view.getMusicView());
                view.getScroll().getViewport().add(model.getPage(currentPage));
                view.setMainMusicView(model.getPage(currentPage));
                setPos(null);
                setCurrentNote(null);
                setCurrentRest(null);

                view.getStatusBar().setText("<html><center>Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");

                if(currentPage==numberOfPages)
                {
                    view.getNextPage().setEnabled(false);
                    view.getNextPageMenu().setEnabled(false);
                }
                if(!view.getPrevPage().isEnabled() && currentPage!=1)
                {
                    view.getPrevPage().setEnabled(true);
                    view.getPrevPageMenu().setEnabled(true);
                }
                int staffCount = model.getStaffCount(currentPage);
                if(staffCount==1)
                {
                    view.getDeleteStaff().setEnabled(false);
                    view.getDeleteStaffButton().setEnabled(false);
                }
                if(!view.getDeleteStaff().isEnabled() && staffCount>1) {
                    view.getDeleteStaff().setEnabled(true);
                    view.getDeleteStaffButton().setEnabled(true);
                }
            }
        });

        //Previous Page Menu functionality specified on menu item selection/button click
        view.getPrevPageMenu().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getMusicView().resetSelect();
                model.prevPage();
                int currentPage = model.getCurrentPage();
                int numberOfPages = model.getNumberOfPages();
                view.getScroll().getViewport().remove(view.getMusicView());
                view.getScroll().getViewport().add(model.getPage(currentPage));
                view.setMainMusicView(model.getPage(currentPage));
                setPos(null);
                setCurrentNote(null);
                setCurrentRest(null);

                view.getStatusBar().setText("<html><center>Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");

                if(currentPage==1)
                {
                    view.getPrevPage().setEnabled(false);
                    view.getPrevPageMenu().setEnabled(false);
                }
                if(!view.getNextPage().isEnabled()&&currentPage!=numberOfPages)
                {
                    view.getNextPage().setEnabled(true);
                    view.getNextPageMenu().setEnabled(true);
                }
                int staffCount = model.getStaffCount(currentPage);
                if(staffCount==1)
                {
                    view.getDeleteStaff().setEnabled(false);
                    view.getDeleteStaffButton().setEnabled(false);
                }
                if(!view.getDeleteStaff().isEnabled() && staffCount>1) {
                    view.getDeleteStaff().setEnabled(true);
                    view.getDeleteStaffButton().setEnabled(true);
                }

            }
        });

        //Add Staff Menu functionality specified on menu item selection/button click
       view.getAddStaff().addActionListener(new ActionListener() {

           @Override
           public void actionPerformed(ActionEvent e) {
               if(currentMode=="Select") {
                   view.getMusicView().resetSelect();
                   setPos(null);
               }
               int currentPage = model.getCurrentPage();
               model.addStaffCount(currentPage);

               view.getMusicView().addStaff();
               int count = model.getStaffCount(currentPage);
               view.getStatusBar().setText("<html><center>Number of Staves: "+count+"<br> Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");
               if(!view.getDeleteStaff().isEnabled() && count>1) {
                   view.getDeleteStaff().setEnabled(true);
                   view.getDeleteStaffButton().setEnabled(true);
               }
           }
       });

        //Delete Staff Menu functionality specified on menu item selection/button click
        view.getDeleteStaff().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentMode=="Select") {
                    view.getMusicView().resetSelect();
                    setPos(null);
                }
                int currentPage = model.getCurrentPage();
                model.deleteStaffCount(currentPage);

                view.getMusicView().deleteStaff();
                int count = model.getStaffCount(currentPage);
                view.getStatusBar().setText("<html><center>Number of Staves: "+count+"<br> Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");

                if(count==1)
                {
                    view.getDeleteStaff().setEnabled(false);
                    view.getDeleteStaffButton().setEnabled(false);
                }

            }
        });

        //New Staff functionality specified on button click
        view.getNewStaff().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentMode=="Select") {
                    view.getMusicView().resetSelect();
                    setPos(null);
                }
                int currentPage = model.getCurrentPage();
                model.addStaffCount(currentPage);

                view.getMusicView().addStaff();
                int count = model.getStaffCount(currentPage);
                view.getStatusBar().setText("<html><center>Number of Staves: "+count+"<br> Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");
                if(!view.getDeleteStaff().isEnabled() && count>1) {
                    view.getDeleteStaff().setEnabled(true);
                    view.getDeleteStaffButton().setEnabled(true);
                }
            }
        });

        //Delete Staff functionality specified on button click
        view.getDeleteStaffButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentMode=="Select") {
                    view.getMusicView().resetSelect();
                    setPos(null);
                }

                int currentPage = model.getCurrentPage();
                model.deleteStaffCount(currentPage);

                view.getMusicView().deleteStaff();
                int count = model.getStaffCount(currentPage);

                view.getStatusBar().setText("<html><center>Number of Staves: "+count+"<br> Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");

                if(count==1)
                {
                    view.getDeleteStaff().setEnabled(false);
                    view.getDeleteStaffButton().setEnabled(false);
                }

            }
        });

        //JSlider functionality specified upon change in value
       view.getNoteLength().addChangeListener(new ChangeListener() {
           @Override
           public void stateChanged(ChangeEvent e) {
               JSlider source = (JSlider) e.getSource();
               int value = (int)source.getValue();
               switch (value){
                   case 0:  { view.getStatusBar().setText("<html><center>Whole currently selected <br> Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>"); setCurrentDuration("Whole");break;}
                   case 10: {view.getStatusBar().setText("<html><center>Half currently selected <br> Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>"); setCurrentDuration("Half");break;}
                   case 20: {view.getStatusBar().setText("<html><center>Quarter currently selected <br> Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");setCurrentDuration("Quarter");break;}
                   case 30: {view.getStatusBar().setText("<html><center>Eighth currently selected <br> Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");setCurrentDuration("Eighth");break;}
                   case 40: {view.getStatusBar().setText("<html><center>Sixteenth currently selected <br> Showing Page "+model.getCurrentPage()+" of "+model.getNumberOfPages()+"<br> Current Mode: "+currentMode+" mode </center></html>");setCurrentDuration("Sixteenth");break;}
               }
           }
       });
       //Combining Note radio button press and drag with change in duration on JSlider
       view.getNote().addMouseListener(new MouseListener() {
           @Override
           public void mouseClicked(MouseEvent e) {

           }

           @Override
           public void mousePressed(MouseEvent e) {
               view.getNote().doClick();
               int y = e.getY();
               setNoteY(y);
               JSlider slider = view.getNoteLength();
               setCurrVal(slider.getValue());
           }

           @Override
           public void mouseReleased(MouseEvent e) {

           }

           @Override
           public void mouseEntered(MouseEvent e) {

           }

           @Override
           public void mouseExited(MouseEvent e) {

           }
       });

       view.getNote().addMouseMotionListener(new MouseMotionListener() {
           @Override
           public void mouseDragged(MouseEvent e) {
               int y = e.getY();
               JSlider slider = view.getNoteLength();

               if(y<noteY+80&&y>noteY+60)
                   slider.setValue(currVal-40);
               if(y<noteY+60&&y>noteY+40)
                   slider.setValue(currVal-30);
               if(y<noteY+40&&y>noteY+20)
                   slider.setValue(currVal-20);
               if(y<noteY+20&&y>noteY)
                   slider.setValue(currVal-10);
               if(y<noteY&&y>noteY-20)
                   slider.setValue(currVal+0);
               if(y<noteY-20&&y>noteY-40)
                   slider.setValue(currVal+10);
               if(y<noteY-40&&y>noteY-60)
                   slider.setValue(currVal+20);
               if(y<noteY-60&&y>noteY-80)
                   slider.setValue(currVal+30);
               if(y<noteY-80&&y>noteY-100)
                   slider.setValue(currVal+40);

           }

           @Override
           public void mouseMoved(MouseEvent e) {

           }
       });
        //Combining Rest radio button press and drag with change in duration on JSlider
        view.getRest().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                view.getRest().doClick();
                int y = e.getY();
                setRestY(y);
                JSlider slider = view.getNoteLength();
                setCurrVal(slider.getValue());
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        view.getRest().addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int y = e.getY();
                JSlider slider = view.getNoteLength();

                if(y<restY+80&&y>restY+60)
                    slider.setValue(currVal-40);
                if(y<restY+60&&y>restY+40)
                    slider.setValue(currVal-30);
                if(y<restY+40&&y>restY+20)
                    slider.setValue(currVal-20);
                if(y<restY+20&&y>restY)
                    slider.setValue(currVal-10);
                if(y<restY&&y>restY-20)
                    slider.setValue(currVal+0);
                if(y<restY-20&&y>restY-40)
                    slider.setValue(currVal+10);
                if(y<restY-40&&y>restY-60)
                    slider.setValue(currVal+20);
                if(y<restY-60&&y>restY-80)
                    slider.setValue(currVal+30);
                if(y<restY-80&&y>restY-100)
                    slider.setValue(currVal+40);

            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
       //Specifying de-selection behavior when click occurs outside MusicView
       view.getControlPanel().addMouseListener(new MouseListener() {
           @Override
           public void mouseClicked(MouseEvent e) {
               view.getMusicView().resetSelect();
           }

           @Override
           public void mousePressed(MouseEvent e) {

           }

           @Override
           public void mouseReleased(MouseEvent e) {

           }

           @Override
           public void mouseEntered(MouseEvent e) {

           }

           @Override
           public void mouseExited(MouseEvent e) {

           }
       });

       view.getFrame().addMouseListener(new MouseListener() {
           @Override
           public void mouseClicked(MouseEvent e) {
               Point point = e.getPoint();
               if(!view.getMusicView().contains(point)) view.getMusicView().resetSelect();
           }

           @Override
           public void mousePressed(MouseEvent e) {

           }

           @Override
           public void mouseReleased(MouseEvent e) {

           }

           @Override
           public void mouseEntered(MouseEvent e) {

           }

           @Override
           public void mouseExited(MouseEvent e) {

           }

       });
    }
}
