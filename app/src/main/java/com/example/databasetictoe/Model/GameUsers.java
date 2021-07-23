package com.example.databasetictoe.Model;

public class GameUsers {

    String myid,enemyid;
    String mygame,enemygame;
    public GameUsers(String myid,String enemyid,String mygame,String enemygame)
    {
        this.myid = myid;
        this.enemyid = enemyid;
        this.mygame = mygame;
        this.enemygame = enemygame;
    }

    public String getMyid() {
        return myid;
    }

    public String getEnemyid() {
        return enemyid;
    }

    public String getMygame() {
        return mygame;
    }

    public String getEnemygame() {
        return enemygame;
    }

    public void setMyid(String myid) {
        this.myid = myid;
    }

    public void setEnemyid(String enemyid) {
        this.enemyid = enemyid;
    }

    public void setMygame(String mygame) {
        this.mygame = mygame;
    }

    public void setEnemygame(String enemygame) {
        this.enemygame = enemygame;
    }
}
