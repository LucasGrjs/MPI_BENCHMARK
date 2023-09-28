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

    // allGather, every proc gather from every proc the same number of data
    // https://www.mpich.org/static/docs/v3.2/www3/MPI_Allgatherv.html
    public static void main(String[] args) throws MPIException
    {        
        MPI.Init(args);
        
        int myself = MPI.COMM_WORLD.getRank(); // rank of current mpi instance
        int tasks = MPI.COMM_WORLD.getSize(); // number of process

        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis() + myself);
        int numberOfIntToSend = rand.nextInt(10) + 1; // number of int to send to others

        int sendSize[] = new int[1]; // buffer to send all data to others
        sendSize[0] = numberOfIntToSend;
        int receiveSize[] = new int[tasks]; // buffer to send all data to others

        int sendData[] = new int[numberOfIntToSend];
        int receiveData[];

        StringBuilder strIn = new StringBuilder("Rank " + myself + " will send " + numberOfIntToSend + " element(s) to everyone else : ");
        for(int index = 0; index < sendData.length; index++)
        {
            sendData[index] = myself; // data to send
            strIn.append(sendData[index] + " ");
        }
        System.out.println(strIn + "\n");

        MPI.COMM_WORLD.allGather(sendSize, 1, MPI.INT, receiveSize, 1, MPI.INT); // receive + send
        
        
        receiveData = new int[Arrays.stream(receiveSize).sum()];
        int displ[] = computeDispl(tasks, receiveSize, myself);
        
        MPI.COMM_WORLD.allGatherv(sendData, sendData.length, MPI.INT, receiveData, receiveSize, displ, MPI.INT); // receive + send
        
        StringBuilder strOut = new StringBuilder("Rank " + myself + " received data : ");
        for(int index = 0; index < receiveData.length; index++)
        {
            strOut.append(receiveData[index] + " ");
        }
        System.out.println(strOut + "\n");
        MPI.Finalize();
    }
}