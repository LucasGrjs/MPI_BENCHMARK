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
import java.nio.*;
import java.net.*;
import java.io.DataOutputStream;
import java.nio.*;

class test
{

    static byte[] wrapperGatherVRoot(byte[] dataFromRoot) throws MPIException
	{

        int totalSize = 0;
        int numberOfProcess = MPI.COMM_WORLD.getSize(); // number of process;

        System.out.println("wrapperGatherVRoot parameter size = " + dataFromRoot.length);
        System.out.println("numberOfProcess = " + numberOfProcess);

        int[] sizeGatherIn = new int[1]; // Buffer to receive all the size from others process
        int[] sizeGatherOut = new int[numberOfProcess]; // Buffer to receive all the size from others process
        
        int[] displ = new int[numberOfProcess]; // displacements buffer => displ[i] = starting index where to write the data from process i in buffer dataBufferOut
        byte[] dataBufferOut; // Buffer to receive all data in root
        
        sizeGatherIn[0] = dataFromRoot.length;
        
        MPI.COMM_WORLD.gather(sizeGatherIn, 1, MPI.INT, sizeGatherOut, 1, MPI.INT, 0); // receive size from all process
        for (int i = 0; i < sizeGatherOut.length; i++) 
        {
            System.out.println( i + " = " + sizeGatherOut[i]);
            totalSize += sizeGatherOut[i];
        }
        System.out.println("total number of data to receive = " + totalSize);

        
        dataBufferOut = new byte[totalSize]; // buffer to receive all the data
        displ[0] = 0; // process 0 can write starting at index 0

        int displIndex = 0;
        for (int processIndex = 1; processIndex < displ.length; processIndex++) 
        {
            for (int j = 0; j < processIndex; j++) 
            {
                displIndex += sizeGatherOut[j];
            }
            System.out.println("Starting index for process " + processIndex +" = " + displIndex);
            displ[processIndex] = displIndex;

            displIndex = 0;
        }

        MPI.COMM_WORLD.gatherv(dataFromRoot, dataFromRoot.length, MPI.BYTE, dataBufferOut, sizeGatherOut, displ, MPI.BYTE, 0); // receive data from all process
        
        for (int index = 0; index < dataBufferOut.length; index++)
        {   
            System.out.println("value = "+ dataBufferOut[index]);
        }
        
        return null;
	}

    public static void main(String[] args) throws MPIException, IOException
    {        
        MPI.Init(args);

        Random rand = new Random();
        int numberOfIntToSend = rand.nextInt(10) + 1; // random number of int to send to root

        int myself = MPI.COMM_WORLD.getRank(); // rank
        int tasks = MPI.COMM_WORLD.getSize(); // number of process
        int totalSize = 0;

        int sizeGatherIn[] = new int[1]; // Buffer to receive all the size from others process
        int sizeGatherOut[] = new int[tasks]; // Buffer to receive all the size from others process

        int[] dataBufferIn = new int[numberOfIntToSend]; // Buffer to send to root
        int[] dataBufferOut; // Buffer to receive all data in root
        int[] displ = new int[tasks]; // displacements buffer => displ[i] = starting index where to write the data from process i in buffer dataBufferOut

        if(myself == 0)
        {
            System.out.println("Number of tasks " + tasks);
        }
        System.out.println(myself + " Number of elements to send = " + numberOfIntToSend);


        for(int i = 0; i < dataBufferIn.length; i++)
        {
            dataBufferIn[i] = myself; // write data in buffer
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        for(int i=0; i < dataBufferIn.length; ++i)
        {
            dos.writeInt(dataBufferIn[i]);
        }

        if(myself == 0) // root receive
        {
            byte[] obj = wrapperGatherVRoot(baos.toByteArray());
        }else // non root send
        {
            sizeGatherIn[0] = baos.toByteArray().length;
            System.out.println("MPI.COMM_WORLD.gather(baos.toByteArray().length = " + sizeGatherIn[0]);
            MPI.COMM_WORLD.gather(sizeGatherIn, 1, MPI.INT, 0); // send size to root
            MPI.COMM_WORLD.gatherv(baos.toByteArray(), baos.toByteArray().length, MPI.BYTE, 0); // send data to root
        }

        MPI.Finalize();
    }
}