import javax.print.attribute.standard.RequestingUserName;

import mpi.*;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class test
{
    // allToall, every proc send numberOfIntToSend element to other proc
    public static void main(String[] args) throws MPIException
    {        
        MPI.Init(args);

        Random rand = new Random();

        int myself = MPI.COMM_WORLD.getRank(); // rank
        int tasks = MPI.COMM_WORLD.getSize(); // number of process
        int numberOfIntToSend = 10000000;  // number of int to send

        int buffSend[] = new int[tasks * numberOfIntToSend];
        int buffSendSize[] = new int[tasks];

        int bufferReceive[] = new int[tasks * numberOfIntToSend];
        buffSendSize[0] = numberOfIntToSend;

        for(int index = 0; index < tasks * numberOfIntToSend; index++)
        {
            buffSend[index] = myself;
        } 
        StringBuilder strSend = new StringBuilder();
        strSend.append("Rank " + myself + " will send " + numberOfIntToSend + " element(s)");

        System.out.println(strSend);

        for(int index = 1; index < tasks; index++)
        {
            buffSendSize[index] = numberOfIntToSend;   
        }

        MPI.COMM_WORLD.allToAll(buffSend, numberOfIntToSend, MPI.INT, bufferReceive, numberOfIntToSend, MPI.INT);
        
        StringBuilder str = new StringBuilder();
        str.append("Rank " + myself + " received : ");
        for(int index = 0; index < tasks * numberOfIntToSend; index++)
        {
            str.append(" " + bufferReceive[index]);
        }

        System.out.println(str);
        
        MPI.Finalize();
    }
}