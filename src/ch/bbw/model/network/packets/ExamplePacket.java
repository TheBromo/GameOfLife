package ch.bbw.model.network.packets;

import java.nio.ByteBuffer;

public class ExamplePacket extends Packet{

    private int row,column;
    private boolean cell;

    public ExamplePacket(){}

    public ExamplePacket(int row, int column, boolean cell) {
        this.row = row;
        this.column = column;
        this.cell = cell;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putInt(row);
        byteBuffer.putInt(column);
        Packet.writeBoolean(cell,byteBuffer);
    }

    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        row=byteBuffer.getInt();
        column=byteBuffer.getInt();
    }
}
