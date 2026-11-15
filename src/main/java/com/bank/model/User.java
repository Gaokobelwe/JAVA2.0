package com.bank.model;

public abstract class User {

// Atributes

    protected String name;
    protected String surname;
    protected String email;
    protected String address;
    protected int cellNo;
    protected int iDnumber;


    //Constructors

    public User(String name, String surname, String email, String address, int cellNo, int iDnumber){

        this.name = name;
        this.surname = surname;
        this.email = email;
        this.address = address;
        this.cellNo = cellNo;
        this.iDnumber = iDnumber;

    }

         protected User() {
     }

    //Methods

       public  void Login(){

        System.out.println("User login");
      };
           public void ViewTrH(){

       };

       public void OpenAccount(){

       };

         public void CloseAccount() {

     }



}
