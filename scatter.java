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
        int numberOfElementsToSendForEach = 10;
        int totalNumberOfElementToSend = tasks * numberOfElementsToSendForEach;

        if(myself == 0)
        { 
            Random rand = new Random();
            int numberOfIntToSend = rand.nextInt(10) + 1; // random number of int to send to root
            System.out.println("Number of tasks : " + tasks);
            System.out.println("process N°" + myself + ", Number of elements to send : " + totalNumberOfElementToSend);

            int[] bufferSend = new int[totalNumberOfElementToSend];
            for(int element = 0; element < totalNumberOfElementToSend; element++)
            {
                bufferSend[element] = element;  
                System.out.println("bufferSend["+element+"] : " + element);
            }
            MPI.COMM_WORLD.scatter(bufferSend, numberOfElementsToSendForEach, MPI.INT, bufferSend, numberOfElementsToSendForEach, MPI.INT, 0);
            System.out.println("ALL SENT");
        }else
        {
            System.out.println("myself : " + myself);
            int[] bufferReceive = new int[numberOfElementsToSendForEach];

            System.out.println("scatter : ");
            MPI.COMM_WORLD.scatter(bufferReceive, numberOfElementsToSendForEach, MPI.INT, bufferReceive, numberOfElementsToSendForEach, MPI.INT, 0);

            System.out.println("result : ");
            for(var auto : bufferReceive)
            {
                System.out.println("auto " + myself + " : " + auto);
            }

        }

        MPI.Finalize();
    }
}