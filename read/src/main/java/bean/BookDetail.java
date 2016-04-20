package bean;

import java.util.List;

/**
 * Created by Administrator on 2016/4/11.
 */
public class BookDetail {

    private List<BookDetailList> booklist;
    private String cover, title, theme, depict, process, author;
    private int id, words,chapterCount;
    private int totalMoney;


    public int getTotalMoney(){

        int a = chapterCount / 10 ;
        a ++ ;
        if(a <= 10){
            totalMoney = a;
        }else if(10 < a && a <= 20){
            totalMoney = 15;
        }else if (a > 20){
            totalMoney = 20;
        }
        return totalMoney;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }

    public List<BookDetailList> getBooklist() {
        return booklist;
    }

    public void setBooklist(List<BookDetailList> booklist) {
        this.booklist = booklist;
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

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getDepict() {
        return depict;
    }

    public void setDepict(String depict) {
        this.depict = depict;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWords() {
        return words;
    }

    public void setWords(int words) {
        this.words = words;
    }



}
