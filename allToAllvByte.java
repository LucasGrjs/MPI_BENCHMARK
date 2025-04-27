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
import java.util.Arrays;

class test
{
    // https://stackoverflow.com/questions/15049190/difference-between-mpi-allgather-and-mpi-alltoall-functions
    /**
     *   
     * fillArrayWithData : fill an array with data according to sizeDataForEach
     * 
     * @param sizeDataForEach : each index = number of int to send to rank N°index
     * @param myself : mpi rank of process calling
     * 
     * @return: int[] filled with correct amount of data to send to other processors
     */
    static int[] fillArrayWithData(int[] sizeDataForEach, int myself)
    {
        StringBuilder strSize = new StringBuilder();
        strSize.append("Sending from rank " + myself + "  : \n");
        for(int index = 0; index < sizeDataForEach.length; index++)
        {
            strSize.append(sizeDataForEach[index] + " element(s) to rank " + index +"\n");
        }
        
        int[] buffData = new int[Arrays.stream(sizeDataForEach).sum()];
        
        int arrayIndex = 0;
        for(int index = 0; index < sizeDataForEach.length; index++)
        {
            //strSize.append("sizeDataForEach["+index+"] : " + sizeDataForEach[index] + "\n ");
            for(int indexData = 0; indexData < sizeDataForEach[index]; indexData++)
            {
                buffData[arrayIndex++] = myself;
            }
        }
        System.out.println(strSize);
        return buffData;
    }

    /**
     * fill a buffer with the size of data to send from dataToSerialize in byte
     * 
     * 
     * @param sizeDataForEach
     * @param dataToSerialize
     * @param myself
     * @param dos
     */
    static int[] fillBufferWithDataByte(int[] sizeDataForEach, int[] dataToSerialize, int myself, DataOutputStream dos) throws IOException
    {
        int[] sizeDataForEachInByte = new int[sizeDataForEach.length]; // buffer to write the size of the serialized object in bytes

        int itemCpt = 0; // can't the number of item for a specific rank
        int currentIndex = 0; // current rank 
        int totalBufferSizeSoFar = 0; // size of dos so far in the computation
        
        for(int index = 0; index < dataToSerialize.length; index++)
        {   
            if(sizeDataForEach[currentIndex] == 0) // we skip empty sized data
            {
                sizeDataForEach[currentIndex] = 0;
                currentIndex++;
            }
            
            itemCpt++;
            dos.writeInt(dataToSerialize[index]);

            if(itemCpt == sizeDataForEach[currentIndex]) // we have serialized all the item for rank currentIndex
            {
                if(currentIndex != 0) // not 0 = all the size of dos - the total buffer size computed so far
                {
                    sizeDataForEachInByte[currentIndex] = dos.size() - totalBufferSizeSoFar;
                }else // 0 = dos.size()
                {
                    sizeDataForEachInByte[currentIndex] = dos.size();
                }
                totalBufferSizeSoFar += sizeDataForEachInByte[currentIndex]; // update of totalBufferSizeSoFar
                currentIndex++; // next rank
                itemCpt = 0; // reset of itemCpt
            }
        }
        
        return sizeDataForEachInByte;
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

    public static void main(String[] args) throws MPIException, IOException
    {        
        MPI.Init(args);

        int myself = MPI.COMM_WORLD.getRank(); // rank of process
        int tasks = MPI.COMM_WORLD.getSize(); // number of process in comm

        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis() + myself);
        
        int bufferReceiveSize[] = new int[tasks]; // buffer to receive size of incoming buffer in allToAllv

        int buffSendSize[] = new int[tasks]; // buffer to send size of incoming buffer to all
        Arrays.setAll(buffSendSize, i -> (900000)); // set buffer with a random int giving the incoming number of element
        
        //buffSendSize[myself] = 0;
        int buffSendData[] = fillArrayWithData(buffSendSize, myself);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        MPI.COMM_WORLD.barrier();
        buffSendSize = fillBufferWithDataByte(buffSendSize, buffSendData, myself, dos); // fill dos and compute size of the buffer to send first

        int displsSend[] = computeDispl(tasks, buffSendSize, myself); // displs of send buffer
        
        MPI.COMM_WORLD.allToAll(buffSendSize, 1, MPI.INT, bufferReceiveSize, 1, MPI.INT); // send to all + receive from all size of incoming buffer
        
        int displsReceive[] = computeDispl(tasks, bufferReceiveSize, myself); // displs of receive buffer


        byte bufferReceiveData[] = new byte[Arrays.stream(bufferReceiveSize).sum()]; // buffer to receive data

        final byte[] final_message = baos.toByteArray();

        System.out.println("total size " + final_message.length +" byte(s)");

        MPI.COMM_WORLD.allToAllv(final_message, buffSendSize, displsSend, MPI.BYTE, bufferReceiveData, bufferReceiveSize, displsReceive, MPI.BYTE); // send to all + receive from all with different size
        
        System.out.println("allToAllv DONE ");
        StringBuilder str = new StringBuilder("----Rank " + myself + " received : \n[");
        IntBuffer intBuf = ByteBuffer.wrap(bufferReceiveData).order(ByteOrder.BIG_ENDIAN).asIntBuffer();

        int[] array = new int[intBuf.remaining()];
        intBuf.get(array);

        for(int index = 0; index < array.length; index++)
        {
            str.append(array[index]+ " "); // read received data
        }
        //System.out.println(str + "]\n");

        System.out.println("number of int received : " + array.length); 
        
        MPI.Finalize();
    }
}