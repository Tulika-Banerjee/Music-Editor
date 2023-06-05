import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Model {

    //MusicView Class defined
    public static class MusicView extends JComponent {

        //List of staves per MusicView object
        private ArrayList<Staff> staves = new ArrayList<>();
        private int preferredHeight;

        //Page Number of the MusicView object
        private int pageNumber;
        //Number of staves on the MusicView object
        private int staffCount=4;

        //MusicView constructor with Page Number and Number of Staves
        public MusicView(int pageNumber, int staffCount) {
            this.pageNumber=pageNumber;
            this.staffCount=staffCount;
            //Defining initial preferred size
            setPreferredSize(new Dimension(1150,552));
            setSize(new Dimension(1150,552));
            //Creating the initial MusicView object by adding four staves
            staves.add(new Staff(72,120));
            staves.add(new Staff(192,240));
            staves.add(new Staff(312,360));
            staves.add(new Staff(432,480));
            this.preferredHeight=552;
            setFocusable(true);
        }

        public int getStaffCount()
        {
            return this.staffCount;
        }
        public void setStaffCount(int staffCount)
        {
            this.staffCount=staffCount;
        }
        public int getNumberOfStaves()
        {
            return this.staves.size();
        }
        public Staff getLastStaff()
        {
            return this.staves.get(this.staves.size() - 1);
        }
        //Adds new Staff to current MusicView
        public void addStaff() {

                Staff lastStaff = this.getLastStaff();
                int y1 = lastStaff.y2+72;
                int y2 = y1+48;
                Staff s = new Staff(y1,y2);
                this.staves.add(s);

                this.incrementHeight();
                repaint();
        }
        //Deletes staff from current MusicView
        public void deleteStaff() {
            this.staves.remove(this.getNumberOfStaves()-1);
            this.decrementHeight();
            repaint();
        }
        //Unselects any selected notes or rests for the given MusicView
        public void resetSelect()
        {
            for(int i=0;i<this.staves.size();i++)
            {
                Staff s = this.staves.get(i);
                for(int j=0;j<s.notes.size();j++)
                {
                    Note n=s.notes.get(j);
                    n.selected=false;
                }
            }
            repaint();
        }
        //Adds a new note to the staff in which the x,y coordinates of the note lie
        public int[] addNote(String duration, int x, int y) {

            int offset = getPositionPoint(duration);
            int y_mid = y+offset;
            Note n = new Note(duration,x,y);
            int staff_index=-1;

            for(int i=0;i<this.staves.size();i++)
            {
                Staff s = this.staves.get(i);
                if(y_mid>s.y1-36 && y_mid<=s.y2+36)
                {
                    staff_index = i;
                    break;
                }
            }
            int[] pos=null;
            if(staff_index!=-1)
            {
                pos=new int[2];
                Staff s = this.staves.get(staff_index);
                s.notes.add(n);
                pos[0]=staff_index;
                pos[1]=s.notes.size()-1;
                repaint();
            }

            return pos;
        }

        //Snaps the note to the nearest legal position
        public void verticalNoteSnapping(String duration, int[] pos)
        {
            int staff_index = pos[0];
            int note_index = pos[1];
            Staff s = this.staves.get(staff_index);
            Note n = this.staves.get(staff_index).notes.get(note_index);
            int offset = getPositionPoint(duration);
            int y_mid = n.y+offset;
            n.pitch=null;

            if(y_mid>=s.y1-36&&y_mid<=s.y1+2) {n.y=s.y1-5-offset; n.pitch="G5";}
            if(y_mid>=s.y1-3&&y_mid<=s.y1+3) {n.y=s.y1-offset; n.pitch="F5";}
            if(y_mid>=s.y1+6-2&&y_mid<=s.y1+6+2) {n.y=s.y1+7-offset; n.pitch="E5";}
            if(y_mid>=s.y1+12-3&&y_mid<=s.y1+12+3) {n.y=s.y1+12-offset;n.pitch="D5";}
            if(y_mid>=s.y1+12+6-2&&y_mid<=s.y1+12+6+2) {n.y=s.y1+12+7-offset; n.pitch="C5";}
            if(y_mid>=s.y1+24-3&&y_mid<=s.y1+24+3) {n.y=s.y1+24-offset;n.pitch="B4";}
            if(y_mid>=s.y1+24+6-2&&y_mid<=s.y1+24+6+2) {n.y=s.y1+24+7-offset; n.pitch="A4";}
            if(y_mid>=s.y1+36-3&&y_mid<=s.y1+36+3) {n.y=s.y1+36-offset;n.pitch="G4";}
            if(y_mid>=s.y1+36+6-2&&y_mid<=s.y1+36+6+2) {n.y=s.y1+36+7-offset; n.pitch="F4";}
            if(y_mid>=s.y1+48-3&&y_mid<=s.y1+48+3) {n.y=s.y1+48-offset;n.pitch="E4";}
            if(y_mid>=s.y2+4&&y_mid<=s.y2+36) {n.y=s.y1+48+7-offset; n.pitch="D4";}

            repaint();
        }
        //Snaps rests to their legal positions
        public void verticalRestSnapping(String duration, int[] pos)
        {
            int staff_index = pos[0];
            int rest_index = pos[1];
            Staff s = this.staves.get(staff_index);
            Rest r = this.staves.get(staff_index).rests.get(rest_index);

            if(duration=="Whole") r.y=s.y1+12-2;
            else if(duration=="Half") r.y=s.y1+12+4;
            else if(duration=="Quarter") r.y=s.y1+10;
            else if(duration=="Eighth") r.y=s.y1+12;
            else if(duration=="Sixteenth") r.y=s.y1+15;
            repaint();
        }
        //Helper function to call Vertical Snapping for a rest when duration is not known
        public void verticalRestSnappingEdit(int[] pos)
        {
            int staff_index = pos[0];
            int rest_index = pos[1];
            Rest r = this.staves.get(staff_index).rests.get(rest_index);
            String duration = r.duration;
            this.verticalRestSnapping(duration,pos);
        }
        //Helper function to call Vertical Snapping for a note when duration is not known
        public void verticalNoteSnappingEdit(int[] pos)
        {
            int staff_index = pos[0];
            int note_index = pos[1];
            Note n = this.staves.get(staff_index).notes.get(note_index);
            String duration = n.duration;
            this.verticalNoteSnapping(duration,pos);
        }
        //Returns the associated pitch for a given Note
        public String getPitch(int[] pos)
        {
            int staff_index = pos[0];
            int note_index = pos[1];
            Note n = this.staves.get(staff_index).notes.get(note_index);
            String pitch = n.pitch;
            return pitch;
        }
        //Updates the Note's coordinates when in initial drag/draw mode
        public void updateNote(int[] pos, int x, int y) {
            int staff_index = pos[0];
            int note_index = pos[1];
            Note n = this.staves.get(staff_index).notes.get(note_index);
            n.x = x;
            n.y = y;
            repaint();
        }
        //Returns the mid-point offsets for each note
        public int getPositionPoint (String duration)
        {
            switch (duration) {
                case "Whole":return 6;
                case "Half": return 42;
                case "Quarter":
                case "Sixteenth":
                    return 43;
                case "Eighth":return 44;
            }
            return 0;
        }

        //Adds a new rest to the staff in which the x,y coordinates of the rest lie
        public int[] addRest(String duration, int x, int y) {

            Rest r = new Rest(duration,x,y);
            int staff_index=-1;
            for(int i=0;i<this.staves.size();i++)
            {
                Staff s = this.staves.get(i);
                if(y>s.y1-36 && y<=s.y2+36)
                {
                    staff_index = i;
                    break;
                }
            }
            int[] pos=null;
            if(staff_index!=-1)
            {
                pos=new int[2];
                Staff s = this.staves.get(staff_index);
                s.rests.add(r);
                repaint();
                pos[0]=staff_index;
                pos[1]=s.rests.size()-1;
            }

            return pos;
        }
        //Updates the Rest's coordinates when in initial drag/draw mode
        public void updateRest(int[] pos, int x, int y) {

            int staff_index = pos[0];
            int rest_index = pos[1];
            Rest r = this.staves.get(staff_index).rests.get(rest_index);
            r.x = x;
            r.y = y;
            repaint();
        }
        //Returns the selected Note or Rest based on point coordinates
        public int[] selectNoteRest(Point point) {

            int[] pos=null;
            for(int i=0;i<this.staves.size();i++)
            {
                Staff s = this.staves.get(i);

                for(int j=0;j<s.notes.size();j++)
                {
                    Note n = s.notes.get(j);
                    if(n.bounds.contains(point))
                    {
                        n.selected=true;
                        pos=new int[3];
                        pos[0]=0;
                        pos[1]=i;
                        pos[2]=j;
                    }
                    else n.selected=false;
                }

                for(int j=0;j<s.rests.size();j++)
                {
                    Rest r = s.rests.get(j);
                    if(r.bounds.contains(point))
                    {
                        r.selected=true;
                        pos=new int[3];
                        pos[0]=1;
                        pos[1]=i;
                        pos[2]=j;
                    }
                    else r.selected=false;
                }
                repaint();
            }
            return pos;
        }
        //Updates the position of the selected note or rest in drag/select mode
        public void editNoteRest(int[] pos,int x, int y)
        {
            int staff_index=pos[1];
            int note_rest_index=pos[2];

            if(pos[0]==0)
            {
                Note n = this.staves.get(staff_index).notes.get(note_rest_index);
                n.x = x;
                n.y = y;
            }

            if(pos[0]==1)
            {
                Rest r = this.staves.get(staff_index).rests.get(note_rest_index);
                r.x = x;
                r.y = y;
            }
            repaint();
        }
        //Associates the note or rest to the current staff after edit is complete
        public int[] setNoteRest(int[] pos)
        {
            int staff_index=pos[1];
            int note_rest_index=pos[2];
            int [] newPos=pos;

            if(pos[0]==0)
            {
                Note n = this.staves.get(staff_index).notes.get(note_rest_index);
                int offset = getPositionPoint(n.duration);
                int y_mid = n.y+offset;
                for(int i=0;i<this.staves.size();i++)
                {
                    Staff s = this.staves.get(i);
                    if(y_mid>s.y1-36 && y_mid<=s.y2+36 && staff_index!=i)
                    {
                        this.staves.get(staff_index).notes.remove(note_rest_index);
                        this.staves.get(i).notes.add(n);
                        newPos[1]=i;
                        newPos[2]=this.staves.get(i).notes.size()-1;
                    }
                }

            }

            if(pos[0]==1)
            {
                Rest r = this.staves.get(staff_index).rests.get(note_rest_index);
                for(int i=0;i<this.staves.size();i++)
                {
                    Staff s = this.staves.get(i);
                    if(r.y>=s.y1-30 && r.y<=s.y2+30 && staff_index!=i)
                    {
                        this.staves.get(staff_index).rests.remove(note_rest_index);
                        this.staves.get(i).rests.add(r);
                        newPos[1]=i;
                        newPos[2]=this.staves.get(i).rests.size()-1;
                    }
                }
            }
            repaint();
            return newPos;
        }
        //Deletes the selected note or rest
        public void deleteNoteRest(int[] pos)
        {
            int staff_index=pos[1];
            int note_rest_index=pos[2];

            if(pos[0]==0)
            {
                this.staves.get(staff_index).notes.remove(note_rest_index);
            }

            if(pos[0]==1)
            {
                this.staves.get(staff_index).rests.remove(note_rest_index);
            }
            repaint();
        }
        //Returns the preferred Height of the given MusicView
        public int getPreferredHeight()
        {
            return this.preferredHeight;
        }
        //Sets the preferred Height of the given MusicView
        public void setPreferredHeight(int height)
        {
            this.preferredHeight=height;
        }
        //Increments the preferred height when new staff is added to given MusicView
        public void incrementHeight()
        {
            int height = this.getPreferredHeight();
            setPreferredSize(new Dimension(1150,height+120));
            setSize(new Dimension(1150,height+120));
            this.setPreferredHeight(height+120);
        }
        //Decrements the preferred height when staff is deleted from given MusicView
        public void decrementHeight()
        {
            int height = this.getPreferredHeight();
            setPreferredSize(new Dimension(1150,height-120));
            setSize(new Dimension(1150,height-120));
            this.setPreferredHeight(height-120);
        }
        //Paints the MusicView component to the screen
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            //Sets background
            g.setColor(Color.WHITE);
            g.fillRect(0,0,getWidth(),getHeight());
            //Paints the staves to the frame by calling the paintComponent function for each Staff
            for(int i=0;i<this.getNumberOfStaves();i++) {
                Staff s = this.staves.get(i);
                s.paintComponent(g);
                //Adds a double bar for the last staff
                if (i == this.getNumberOfStaves() - 1) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setStroke(new BasicStroke(4));
                    g2.draw(new Line2D.Float(1100, this.staves.get(i).y1 + 1.5F, 1100, this.staves.get(i).y2 - 2));
                    g2.setStroke(new BasicStroke(1));
                    g.drawLine(1090, this.staves.get(i).y1, 1090, this.staves.get(i).y2);
                }
                //Paints the notes associated with each staff to the frame by calling the paintComponent function for each Note
                if (s.notes != null) {
                    for (int j = 0; j < s.notes.size(); j++) {
                        Note n = s.notes.get(j);
                        n.paintComponent(g);
                        //Highlights Note if selected
                        if(n.selected==true)
                        {

                            Graphics2D g2 = (Graphics2D) g;
                            g2.setColor(Color.blue);
                            g2.draw(n.bounds);
                        }
                    }
                }
                //Paints the rests associated with each staff to the frame by calling the paintComponent function for each Rest
                if (s.rests != null) {
                    for (int j = 0; j < s.rests.size(); j++) {
                        Rest r = s.rests.get(j);
                        r.paintComponent(g);
                        //Highlights Rest if selected
                        if(r.selected==true)
                        {

                            Graphics2D g2 = (Graphics2D) g;
                            g2.setColor(Color.blue);
                            g2.draw(r.bounds);
                        }
                    }
                }
            }

        }
    }
    //Class Staff defined
    public static class Staff extends JComponent{

        private int x1=50;
        private int y1;
        private int x2=1100;
        private int y2;
        //Each staff has a list of notes and rests
        private ArrayList<Note> notes = new ArrayList<>();
        private  ArrayList<Rest> rests = new ArrayList<>();

        public Staff(int y1,int y2) {
            this.y1=y1;
            this.y2=y2;
        }

        //PaintComponent called by MusicView to paint each staff to screen
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);

            //Horizontal lines
            g.drawLine(x1,y1,x2,y1);
            g.drawLine(x1,y1+12,x2,y1+12);
            g.drawLine(x1,y1+24,x2,y1+24);
            g.drawLine(x1,y1+36,x2,y1+36);
            g.drawLine(x1,y1+48,x2,y1+48);
            //Vertical Lines
            g.drawLine(x1,y1,x1,y2);
            g.drawLine(x1+300,y1,x1+300,y2);
            g.drawLine(x1+550,y1,x1+550,y2);
            g.drawLine(x1+800,y1,x1+800,y2);
            g.drawLine(x1+1050,y1,x1+1050,y2);

            //Painting the Treble Clef and common time images
            try{
                BufferedImage trebleClefImage = ImageIO.read(getClass().getResource("/images/images/trebleClef.png"));
                g.drawImage(trebleClefImage, x1, y1-20, null); }
            catch (Exception e) {System.out.println(e);}

            try{
                Image commonTimeImage = ImageIO.read(getClass().getResource("/images/images/commonTime.png"));
                Image new_img = commonTimeImage.getScaledInstance(20,36, Image.SCALE_SMOOTH);
                g.drawImage(new_img, x1+50, y1+6, null); }
            catch (Exception e) {System.out.println(e);}

        }

    }
    //Class Note defined
    public static class Note extends JComponent{

        private int x;
        private int y;
        private String duration;

        private Rectangle bounds;

        private Boolean selected = false;
        private String pitch;

        public Note(String duration,int x, int y) {
            this.x=x;
            this.y=y;
            this.duration=duration;
        }
        //PaintComponent called by MusicView to paint each note to screen
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            //Loading, scaling and painting images for each note
            if(duration=="Sixteenth")
            {
                try{
                    BufferedImage sixteenthNoteImage = ImageIO.read(getClass().getResource("/images/images/sixteenthNote.png"));
                    Image new_img = sixteenthNoteImage.getScaledInstance(22,48, Image.SCALE_SMOOTH);
                    g.drawImage(new_img, this.x, this.y, null);
                    this.bounds= new Rectangle(this.x,this.y,new_img.getWidth(null),new_img.getHeight(null));
                }
                catch (Exception e) {System.out.println(e);}
            }

            if(duration=="Eighth")
            {
                try{
                    BufferedImage eighthNoteImage = ImageIO.read(getClass().getResource("/images/images/eighthNote.png"));
                    Image new_img = eighthNoteImage.getScaledInstance(40,48, Image.SCALE_SMOOTH);
                    g.drawImage(new_img, this.x, this.y, null);
                    this.bounds= new Rectangle(this.x,this.y,new_img.getWidth(null),new_img.getHeight(null));
                    }
                catch (Exception e) {System.out.println(e);}
            }

            if(duration=="Quarter")
            {
                try{
                    BufferedImage quarterNoteImage = ImageIO.read(getClass().getResource("/images/images/quarterNote.png"));
                    Image new_img = quarterNoteImage.getScaledInstance(13,48, Image.SCALE_SMOOTH);
                    g.drawImage(new_img, this.x, this.y, null);
                    this.bounds= new Rectangle(this.x,this.y,new_img.getWidth(null),new_img.getHeight(null));
                }
                catch (Exception e) {System.out.println(e);}
            }

            if(duration=="Half")
            {
                try{
                    BufferedImage halfNoteImage = ImageIO.read(getClass().getResource("/images/images/halfNote.png"));
                    Image new_img = halfNoteImage.getScaledInstance(30,49, Image.SCALE_SMOOTH);
                    g.drawImage(new_img, this.x, this.y, null);
                    this.bounds= new Rectangle(this.x,this.y,new_img.getWidth(null),new_img.getHeight(null));
                }
                catch (Exception e) {System.out.println(e);}
            }

            if(duration=="Whole")
            {
                try{
                    BufferedImage wholeNoteImage = ImageIO.read(getClass().getResource("/images/images/wholeNote.png"));
                    g.drawImage(wholeNoteImage, this.x, this.y, null);
                    this.bounds= new Rectangle(this.x,this.y,wholeNoteImage.getWidth(),wholeNoteImage.getHeight());
                }
                catch (Exception e) {System.out.println(e);}
            }

        }
    }
    //Class Rest defined
    public static class Rest extends JComponent{

        private int x;
        private int y;
        private String duration;
        private boolean selected = false;
        private Rectangle bounds;

        public Rest(String duration,int x, int y) {
            this.x=x;
            this.y=y;
            this.duration=duration;
        }
        //PaintComponent called by MusicView to paint each rest to screen
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            //Loading, scaling and painting images for each rest
            if (duration == "Sixteenth") {
                try {
                    BufferedImage sixteenthRestImage = ImageIO.read(getClass().getResource("/images/images/sixteenthRest.png"));
                    Image new_img = sixteenthRestImage.getScaledInstance(16,34, Image.SCALE_SMOOTH);
                    g.drawImage(new_img, this.x, this.y, null);
                    this.bounds= new Rectangle(this.x,this.y,new_img.getWidth(null),new_img.getHeight(null));
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            if (duration == "Eighth") {
                try {
                    BufferedImage eighthRestImage = ImageIO.read(getClass().getResource("/images/images/eighthRest.png"));
                    Image new_img = eighthRestImage.getScaledInstance(12,25, Image.SCALE_SMOOTH);
                    g.drawImage(new_img, this.x, this.y, null);
                    this.bounds= new Rectangle(this.x,this.y,new_img.getWidth(null),new_img.getHeight(null));
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            if (duration == "Quarter") {
                try {
                    BufferedImage quarterRestImage = ImageIO.read(getClass().getResource("/images/images/quarterRest.png"));
                    Image new_img = quarterRestImage.getScaledInstance(11,32, Image.SCALE_SMOOTH);
                    g.drawImage(new_img, this.x, this.y, null);
                    this.bounds= new Rectangle(this.x,this.y,new_img.getWidth(null),new_img.getHeight(null));
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            if (duration == "Half") {
                try {
                    BufferedImage halfRestImage = ImageIO.read(getClass().getResource("/images/images/halfRest.png"));
                    g.drawImage(halfRestImage, this.x, this.y, null);
                    this.bounds= new Rectangle(this.x,this.y,halfRestImage.getWidth(),halfRestImage.getHeight());
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            if (duration == "Whole") {
                try {
                    BufferedImage wholeRestImage = ImageIO.read(getClass().getResource("/images/images/wholeRest.png"));
                    g.drawImage(wholeRestImage, this.x, this.y, null);
                    this.bounds= new Rectangle(this.x,this.y,wholeRestImage.getWidth(),wholeRestImage.getHeight());
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    //ArrayList of MusicViews to store pages, currentPage holds page number of current Page
    private ArrayList<MusicView> pages = new ArrayList<>();
    int currentPage=1;
    //Returns the current MusicView object/Page by page number
    public MusicView getPage(int pageNumber)
    {
        return this.pages.get(pageNumber-1);
    }
    //Returns the number of staves for the given page number
    public int getStaffCount(int pageNumber)
    {
        return this.pages.get(pageNumber-1).getStaffCount();
    }
    //Sets the number of staves for the given page number
    public void setStaffCount(int pageNumber, int staffCount)
    {
        this.pages.get(pageNumber-1).setStaffCount(staffCount);
    }
    //Increases the number of staves by one for the given page number
    public void addStaffCount(int pageNumber)
    {
        int currentStaffCount = this.pages.get(pageNumber-1).getStaffCount();
        this.setStaffCount(pageNumber,currentStaffCount+1);
    }
    //Decreases the number of staves by one for the given page number
    public void deleteStaffCount(int pageNumber)
    {
        int currentStaffCount = this.pages.get(pageNumber-1).getStaffCount();
        this.setStaffCount(pageNumber,currentStaffCount-1);
    }
    //Returns the total number of pages
    public int getNumberOfPages(){
        return this.pages.size();
    }
    //Adds a new page (MusicView object) to the Arraylist of MusicViews
    public void addPage(MusicView newPage){
        this.pages.add(newPage);
    }
    //Removes the page given its page number from the Arraylist of MusicViews
    public void deletePage(int pageNumber){
        this.pages.remove(pageNumber-1);
    }
    //Returns the Arraylist of MusicViews
    public ArrayList<MusicView> getPages()
    {
        return this.pages;
    }
    //Returns the current page number
    public int getCurrentPage()
    {
        return this.currentPage;
    }
    //Sets the current page number
    public void setCurrentPage(int pageNumber)
    {
        this.currentPage=pageNumber;
    }
    //Increments current page number by one
    public void nextPage()
    {
        currentPage=currentPage+1;
    }
    //Decrements current page number by one
    public void prevPage()
    {
        currentPage=currentPage-1;
    }
}
