package org.jorlib.demo.frameworks.columnGeneration.branchAndPriceExample;

/**
 * Created by jkinable on 5/2/15.
 */
public class Test {
    public Test(){
        System.out.println("test1");
        if(1==1)
            return;

        try{
            System.out.println("throwing nullpointer");
            throw new NullPointerException();
        }catch(NullPointerException e){
            System.out.println("Caught nullpointer");
        }finally{
            System.out.println("Executing finally");
        }

    }

    public static void main(String[] args){
        new Test();
    }
}
