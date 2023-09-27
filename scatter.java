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
    // Scatter from every proc to other
    public static void main(String[] args) throws MPIException
    {        
        MPI.Init(args);

        int myself = MPI.COMM_WORLD.getRank(); // rank
        int tasks = MPI.COMM_WORLD.getSize(); // number of process
        int numberOfIntToSend = 10;
        int totalNumberOfElementToSend = tasks * numberOfIntToSend;
        int[] bufferReceive = new int[numberOfIntToSend];

        int targetRank = 3; // rank to send data to
        if(targetRank >= tasks)
        {
            targetRank = 0;
        }

        if(myself == targetRank)
        { 
            StringBuilder str = new StringBuilder();
            str.append("Rank " + myself + " will send " + numberOfIntToSend + " element(s) to everyone else ");
            System.out.println(str + "\n");
            str.setLength(0);
            
            int[] bufferSend = new int[totalNumberOfElementToSend];
            for(int element = 0; element < totalNumberOfElementToSend; element++)
            {
                bufferSend[element] = element;  
            }
            MPI.COMM_WORLD.scatter(bufferSend, numberOfIntToSend, MPI.INT, bufferReceive, numberOfIntToSend, MPI.INT, targetRank);

            str.append("Rank " + myself + " received : ");
            for(var auto : bufferReceive)
            {
                str.append(auto + " ");
            }
            System.out.println(str + "\n");
        }else
        {
            MPI.COMM_WORLD.scatter(bufferReceive, numberOfIntToSend, MPI.INT, bufferReceive, numberOfIntToSend, MPI.INT, targetRank);

            
            StringBuilder str = new StringBuilder();
            str.append("Rank " + myself + " received : ");
            for(var auto : bufferReceive)
            {
                str.append(auto + " ");
            }
            System.out.println(str + "\n");
        }

        MPI.Finalize();
    }
}