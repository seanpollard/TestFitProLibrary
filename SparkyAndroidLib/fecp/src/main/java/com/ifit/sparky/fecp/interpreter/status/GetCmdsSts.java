/**
 * This status handles the Get Supported Commands status
 * @author Levi.Balling
 * @date 12/16/13
 * @version 1
 * This status must receive the reply from the Get Supported Commands command
 */
package com.ifit.sparky.fecp.interpreter.status;

import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

public class GetCmdsSts extends Status implements StatusInterface, Serializable {

    private static final int MIN_STS_LENGTH = 5;
    private HashSet<CommandId> mCmdList;
    /**
     * Main constructor for the Get supported commands
     * @param devId the device Id of the expected Status
     * @throws Exception if things don't match up.
     */
    public GetCmdsSts(DeviceId devId) throws InvalidStatusException
    {
        super(StatusId.DEV_NOT_SUPPORTED, MIN_STS_LENGTH, CommandId.GET_SUPPORTED_COMMANDS, devId);
        this.mCmdList = new HashSet<CommandId>();
    }

    /**
     * Handles the message that is coming across the usb. It handles raw data, and it
     * must be handled by the correct status.
     *
     * @param buff the msg that came from the usb. only str
     */
    @Override
    public void handleStsMsg(ByteBuffer buff) throws Exception
    {
        super.handleStsMsg(buff);

        //now parse the data
        //does not count master generic commands
        if(this.getStsId() == StatusId.DONE)
        {
            //check get the size of the data
            for(int i = MIN_STS_LENGTH; i < this.getLength(); i++)
            {
                this.mCmdList.add(CommandId.getCommandId(buff.get()));
            }
        }
    }

    /**
     * Gets the set of supported Commands
     * @return the Command Ids that are supported
     */
    public Set<CommandId> getSupportedCommands()
    {
        return this.mCmdList;
    }
}
