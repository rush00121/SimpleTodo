package com.codepath.simpletodo.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by rushabh on 4/4/15.
 */
@Table(name="TodoItems")
public class Item extends Model {

    // This is the unique id given by the server
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long remoteId;

    @Column(name = "task")
    public String task;

    @Column(name = "dueDate")
    public String date;

    public Item() {
        super();
    }

    public Item(String task) {
        super();
        this.task = task;
    }

    public Item(String task, String date) {
        super();
        this.task = task;
        this.date = date;
    }

    @Override
    public String toString() {
        return task + (date!=null?(":" + date):"");
    }
}
