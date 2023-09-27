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
    static int[] computeDispl(int tasks, int[] buffSendSize, int myself)
    {
        int[] displs = new int[tasks]; // displacements buffer => displ[i] = starting index where to write the data from process i in buffer dataBufferOut
        displs[0] = 0;
        
        StringBuilder str = new StringBuilder("computeDispl : " + myself + " : "+ "\n");
        str.append("displs" + 0 + " :: " + displs[0]+ "\n");

        for(int index = 1; index < buffSendSize.length; index++)
        {
            str.append(index + " :: " + buffSendSize[index]+ "\n");
            displs[index] = displs[index-1] + buffSendSize[index-1];
            str.append("displs" + index + " :: " + displs[index]+ "\n");
        }
        //System.out.println(str);
        return displs;
    }

    static byte[] wrapperGatherVRoot(byte[] dataFromRoot, int tasks, int myself) throws MPIException
	{

        int totalSize = 0;
        int numberOfProcess = MPI.COMM_WORLD.getSize(); // number of process;

        //System.out.println("wrapperGatherVRoot parameter size = " + dataFromRoot.length);
        //System.out.println("numberOfProcess = " + numberOfProcess);

        int[] sizeGatherIn = new int[1]; // Buffer to receive all the size from others process
        int[] sizeGatherOut = new int[numberOfProcess]; // Buffer to receive all the size from others process
        
        int[] displ = new int[numberOfProcess]; // displacements buffer => displ[i] = starting index where to write the data from process i in buffer dataBufferOut
        byte[] dataBufferOut; // Buffer to receive all data in target rank
        
        sizeGatherIn[0] = dataFromRoot.length;
        
        MPI.COMM_WORLD.gather(sizeGatherIn, 1, MPI.INT, sizeGatherOut, 1, MPI.INT, myself); // receive size from all process
        for (int i = 0; i < sizeGatherOut.length; i++) 
        {
            //System.out.println( i + " = " + sizeGatherOut[i]);
            totalSize += sizeGatherOut[i];
        }
        //System.out.println("total number of data to receive = " + totalSize);

        
        dataBufferOut = new byte[totalSize]; // buffer to receive all the data
        displ = computeDispl(tasks, sizeGatherOut, myself);

        MPI.COMM_WORLD.gatherv(dataFromRoot, dataFromRoot.length, MPI.BYTE, dataBufferOut, sizeGatherOut, displ, MPI.BYTE, myself); // receive data from all process

        IntBuffer intBuf = ByteBuffer.wrap(dataBufferOut).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
        int[] array = new int[intBuf.remaining()];
        intBuf.get(array);

        StringBuilder str = new StringBuilder("---------RECV---------\n");
        str.append("Rank " + myself + " received : ");
        for(int index = 0; index < array.length; index++)
        {
            str.append(" " + array[index]); // read received data
        }
        System.out.println(str);
        
        return null;
	}

    public static void main(String[] args) throws MPIException, IOException
    {        
        MPI.Init(args);

        Random rand = new Random();
        int numberOfIntToSend = rand.nextInt(10) + 1; // random number of int to send to root

        int myself = MPI.COMM_WORLD.getRank(); // rank of current mpi instance
        int tasks = MPI.COMM_WORLD.getSize(); // number of process
        int totalSize = 0;

        int targetRank = 3; // rank to send data to
        if(targetRank >= tasks)
        {
            targetRank = 0;
        }

        int sizeGatherIn[] = new int[1]; // Buffer to receive all the size from others process
        int sizeGatherOut[] = new int[tasks]; // Buffer to receive all the size from others process

        int[] dataBufferIn = new int[numberOfIntToSend]; // Buffer to send to root
        int[] dataBufferOut; // Buffer to receive all data in root
        int[] displ = new int[tasks]; // displacements buffer => displ[i] = starting index where to write the data from process i in buffer dataBufferOut

        StringBuilder str = new StringBuilder();
        str.append("Rank " + myself + " will send " + numberOfIntToSend + " element(s) to rank " +  targetRank + " : ");
        for(int i = 0; i < dataBufferIn.length; i++)
        {
            dataBufferIn[i] = myself; // write data in buffer
            str.append(" " + dataBufferIn[i]);
        }
        System.out.println(str);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        for(int i=0; i < dataBufferIn.length; ++i)
        {
            dos.writeInt(dataBufferIn[i]);
        }

        if(myself == targetRank) // rank to receive
        {
            byte[] obj = wrapperGatherVRoot(baos.toByteArray(), tasks, myself);
        }else // non root send
        {
            sizeGatherIn[0] = baos.toByteArray().length;
            //System.out.println("MPI.COMM_WORLD.gather(baos.toByteArray().length = " + sizeGatherIn[0]);
            MPI.COMM_WORLD.gather(sizeGatherIn, 1, MPI.INT, targetRank); // send size to root
            MPI.COMM_WORLD.gatherv(baos.toByteArray(), baos.toByteArray().length, MPI.BYTE, targetRank); // send data to root
        }

        MPI.Finalize();
    }
}