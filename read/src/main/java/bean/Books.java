package bean;

/**
 * Created by Administrator on 2016/4/8.
 */
public class Books {

    private String author,cover,title;
    private int id,chapterCount;


    public int getTotalMoney(){
        int a = chapterCount / 10;
        a++;
        if(a <= 10){
            return a;
        }else if (a > 10 && a <= 20){
            return 15;
        }else if(a > 20)
            return 20;
        return 0;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }

    @Override
    public String toString() {
        return "Books{" +
                "author='" + author + '\'' +
                ", cover='" + cover + '\'' +
                ", title='" + title + '\'' +
                ", id=" + id +
                ", chapterCount=" + chapterCount +
                '}';
    }
}
