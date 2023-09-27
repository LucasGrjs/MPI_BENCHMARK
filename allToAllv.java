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
import java.util.Arrays;

class test
{
    // https://stackoverflow.com/questions/15049190/difference-between-mpi-allgather-and-mpi-alltoall-functions

    // all proc send random size data and receive random size data
    static int[] fillArrayWithData(int[] sizeDataForEach, int myself)
    {
        StringBuilder strSize = new StringBuilder();
        strSize.append("Sending from rank " + myself + "  : \n");
        for(int index = 0; index < sizeDataForEach.length; index++)
        {
            strSize.append(sizeDataForEach[index] + " element(s) to " + index +" \n");
        }
        System.out.println(strSize);
        
        int[] buffData = new int[Arrays.stream(sizeDataForEach).sum()];
        
        int arrayIndex = 0;
        for(int index = 0; index < sizeDataForEach.length; index++)
        {
            for(int indexData = 0; indexData < sizeDataForEach[index]; indexData++)
            {
                buffData[arrayIndex++] = myself;
            }
        }
        return buffData;
    }

    static int[] computeDispl(int tasks, int[] buffSendSize, int myself)
    {
        int[] displs = new int[tasks];
        displs[0] = 0;

        for(int index = 1; index < buffSendSize.length; index++)
        {
            displs[index] = displs[index-1] + buffSendSize[index-1];
        }
        return displs;
    }

    public static void main(String[] args) throws MPIException
    {        
        MPI.Init(args);

        int myself = MPI.COMM_WORLD.getRank(); // rank of process
        int tasks = MPI.COMM_WORLD.getSize(); // number of process in comm

        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis() + myself);
        
        int bufferReceiveSize[] = new int[tasks]; // buffer to receive size of incoming buffer in allToAllv
        
        int buffSendSize[] = new int[tasks]; // buffer to send size of incoming buffer to all
        Arrays.setAll(buffSendSize, i -> rand.nextInt(10) + 1); // set buffer with a random int giving the incoming number of element
        buffSendSize[myself] = 0;

        int buffSendData[] = fillArrayWithData(buffSendSize, myself); // fill buffer with data to send

        MPI.COMM_WORLD.allToAll(buffSendSize, 1, MPI.INT, bufferReceiveSize, 1, MPI.INT); // send to all + receive from all size of incoming buffer

        int bufferReceiveData[] = new int[Arrays.stream(bufferReceiveSize).sum()]; // buffer to receive data
        int displsSend[] = computeDispl(tasks, buffSendSize, myself); // displs of send buffer
        int displsReceive[] = computeDispl(tasks, bufferReceiveSize, myself); // displs of receive buffer

        MPI.COMM_WORLD.allToAllv(buffSendData, buffSendSize, displsSend, MPI.INT, bufferReceiveData, bufferReceiveSize, displsReceive, MPI.INT); // send to all + receive from all with different size
        
        StringBuilder str = new StringBuilder();
        str.append("Rank " + myself + " received : ");
        for(int index = 0; index < bufferReceiveData.length; index++)
        {
            str.append(" " + bufferReceiveData[index]); // read received data
        }

        System.out.println(str);
        
        MPI.Finalize();
    }
}