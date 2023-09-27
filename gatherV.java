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
    // Proc 0 gather a random of element from each proc
    public static void main(String[] args) throws MPIException
    {        
        MPI.Init(args);

        Random rand = new Random();
        int numberOfIntToSend = rand.nextInt(10) + 1; // random number of int to send to root

        int myself = MPI.COMM_WORLD.getRank(); // rank
        int tasks = MPI.COMM_WORLD.getSize(); // number of process
        int totalSize = 0;

        int targetRank = 3; // rank to send data to
        if(targetRank >= tasks)
        {
            targetRank = 0;
        }

        int sizeGatherIn[] = new int[1]; // buffer to send the size to root
        sizeGatherIn[0] = numberOfIntToSend;

        int sizeGatherOut[] = new int[tasks]; // Buffer to receive all the size from others process

        int[] dataBufferIn = new int[numberOfIntToSend]; // Buffer to send to root
        int[] dataBufferOut; // Buffer to receive all data in root
        int[] displ = new int[tasks]; // displacements buffer => displ[i] = starting index where to write the data from process i in buffer dataBufferOut

        System.out.println("Rank " + myself + " will send " + numberOfIntToSend + " element(s) to rank " + targetRank);

        for(int i = 0; i < dataBufferIn.length; i++)
        {
            dataBufferIn[i] = myself; // write data in buffer
        }

        if(myself == targetRank) // root gathering data
        {
            MPI.COMM_WORLD.gather(sizeGatherIn, 1, MPI.INT, sizeGatherOut, 1, MPI.INT, targetRank); // receive size from all process

            for (int i = 0; i < sizeGatherOut.length; i++) 
            {
                totalSize += sizeGatherOut[i]; // setup buffer size for gatherV
            }

            dataBufferOut = new int[totalSize]; // buffer to receive all the data
            displ[0] = 0; // process 0 can write starting at index 0

            int displIndex = 0;
            for (int processIndex = 1; processIndex < displ.length; processIndex++) 
            {
                for (int j = 0; j < processIndex; j++) 
                {
                    displIndex += sizeGatherOut[j];
                }
                displ[processIndex] = displIndex; // compute displacements in the buffer

                displIndex = 0;
            }

            MPI.COMM_WORLD.gatherv(dataBufferIn, numberOfIntToSend, MPI.INT, dataBufferOut, sizeGatherOut, displ, MPI.INT, targetRank); // receive data from all process
            
            StringBuilder str = new StringBuilder("Rank " + myself + " received : ");
            for(int index = 0; index < dataBufferOut.length; index++)
            {
                str.append(" " + dataBufferOut[index]);
            }
            System.out.println(str);
        
        }else // others sending data
        {
            MPI.COMM_WORLD.gather(sizeGatherIn, 1, MPI.INT, targetRank); // send size to root
            MPI.COMM_WORLD.gatherv(dataBufferIn, numberOfIntToSend, MPI.INT, targetRank); // send data to root
        }

        MPI.Finalize();
    }
}