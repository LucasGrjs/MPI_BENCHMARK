import javax.print.attribute.standard.RequestingUserName;

import mpi.*;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class test
{
    // gather numberOfIntToSend int from other proc on targetRank
    public static void main(String[] args) throws MPIException
    {
        MPI.Init(args);
        
        int numberOfIntToSend = 7; // number of int to send to target rank
        int myself = MPI.COMM_WORLD.getRank(); // rank of current mpi instance
        int tasks = MPI.COMM_WORLD.getSize(); // number of process

        int out[] = new int[numberOfIntToSend]; // buffer to send all data to target rank

        int targetRank = 2; // rank to send data to
        if(targetRank >= tasks)
        {
            targetRank = 0;
        }

        StringBuilder str = new StringBuilder();

        str.append("Rank " + myself + " will send " + numberOfIntToSend + " element(s) to rank " + targetRank + " : " + "\n");
        for(int i = 0; i < numberOfIntToSend; i++)
        {
            out[i] = myself; // will buffer with data
            str.append(out[i] + " ");
        }
        
        if(myself == targetRank) // root gathering data
        {
            int in[] = new int[numberOfIntToSend * tasks]; // buffer to receive all data
            MPI.COMM_WORLD.gather(out, numberOfIntToSend, MPI.INT, in, numberOfIntToSend, MPI.INT, targetRank); // receive
            
            str.append("\n\n-----------RECV----------- " + "\n");
            
            str.append("Rank " + myself + " received : \n");
            for (int i = 0; i < in.length; i++) {
                str.append(in[i] + " ");
            }
        }else // others sending data
        {
            MPI.COMM_WORLD.gather(out, numberOfIntToSend, MPI.INT, targetRank); // send 
        }
        
        System.out.println(str + "\n");

        MPI.Finalize();
    }
}