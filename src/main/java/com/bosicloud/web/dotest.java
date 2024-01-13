package com.bosicloud.web;

public class dotest {

    public static void main(String[] args) {

       String SW8 = "1-N2Q5ZTY2ODJkNjI1NGU1YWFiYzk2MGI5MmVhNWQ1NjQuMTA1LjE2NTUxNzcyNTIzNTIwMDE1-N2Q5ZTY2ODJkNjI1NGU1YWFiYzk2MGI5MmVhNWQ1NjQuMTA1LjE2NTUxNzcyNTIzNTIwMDE0-0-bWlrLW9wcy1CLVNlcnZpY2U=-bWlrLW9wcy1CLVNlcnZpY2UtaW5zdGFuY2U=-L2V1cmVrYS9hcHBzL2RlbHRh-bG9jYWxob3N0OjcwNzA=";
        String strHeadValue = SW8;
        String[] strsHeadValue = strHeadValue.split("=-");

    /*    for (int i = 0; i < strsHeadValue.length; i++) {
            System.out.println("1.2、strsHeadValue[" + i + "]" + strsHeadValue[i].toString() + "\n");
            System.out.println("1.2、Base64.decode2UTFString( this.headValue.toString() )：： [" + i + "]" + Base64.decode2UTFString( strsHeadValue[i].toString()) );
            //find  authorization
        }*/

        int i =1;


        //System.out.println("1.2、Base64.decode2UTFString( this.headValue.toString() )：： [" + i + "]" + Base64.decode2UTFString( strsHeadValue[strsHeadValue.length-1].toString()) );

        System.out.println( strsHeadValue.length );
        String  teststr1 =  strsHeadValue[strsHeadValue.length-1].toString();
        String  teststr2 =  strsHeadValue[strsHeadValue.length-2].toString();

        System.out.println( teststr1.replace("-","") );
        System.out.println( teststr2 );

        System.out.println("1.2、Base64.decode2UTFString( this.headValue.toString() )：： [" + i + "]" + Base64.decode2UTFString(  teststr1.replace("-","")  ) );
        System.out.println("1.2、Base64.decode2UTFString( this.headValue.toString() )：： [" + i + "]" + Base64.decode2UTFString(  teststr2 ) );



    }
}
