// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/8/2013 10:58:54 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   HFPage.java

package heap;

import global.*;
import java.io.PrintStream;

public class HFPage extends Page
{

    public HFPage()
    {
        initDefaults();
    }

    public HFPage(Page page)
    {
        super(page.getData());
    }

    protected void initDefaults()
    {
        setShortValue((short)0, 0);
        setShortValue((short)1024, 2);
        setShortValue((short)1004, 4);
        setShortValue((short)0, 6);
        setIntValue(-1, 8);
        setIntValue(-1, 12);
        setIntValue(-1, 16);
    }

    public short getSlotCount()
    {
        return getShortValue(0);
    }

    public short getFreeSpace()
    {
        return getShortValue(4);
    }

    public short getType()
    {
        return getShortValue(6);
    }

    public void setType(short type)
    {
        setShortValue(type, 6);
    }

    public PageId getPrevPage()
    {
        return new PageId(getIntValue(8));
    }

    public void setPrevPage(PageId pageno)
    {
        setIntValue(pageno.pid, 8);
    }

    public PageId getNextPage()
    {
        return new PageId(getIntValue(12));
    }

    public void setNextPage(PageId pageno)
    {
        setIntValue(pageno.pid, 12);
    }

    public PageId getCurPage()
    {
        return new PageId(getIntValue(16));
    }

    public void setCurPage(PageId pageno)
    {
        setIntValue(pageno.pid, 16);
    }

    public short getSlotLength(int slotno)
    {
        return getShortValue(20 + slotno * 4);
    }

    public short getSlotOffset(int slotno)
    {
        return getShortValue(20 + slotno * 4 + 2);
    }

    public RID insertRecord(byte record[])
    {
        short recLength = (short)record.length;
        int spaceNeeded = recLength + 4;
        short freeSpace = getShortValue(4);
        if(spaceNeeded > freeSpace)
            return null;
        short slotCnt = getShortValue(0);
        short i;
        for(i = 0; i < slotCnt; i++)
        {
            short length = getSlotLength(i);
            if(length == -1)
                break;
        }

        if(i == slotCnt)
        {
            freeSpace -= spaceNeeded;
            setShortValue(freeSpace, 4);
            slotCnt++;
            setShortValue(slotCnt, 0);
        } else
        {
            freeSpace -= recLength;
            setShortValue(freeSpace, 4);
        }
        short usedPtr = getShortValue(2);
        usedPtr -= recLength;
        setShortValue(usedPtr, 2);
        int slotpos = 20 + i * 4;
        setShortValue(recLength, slotpos);
        setShortValue(usedPtr, slotpos + 2);
        System.arraycopy(record, 0, data, usedPtr, recLength);
        return new RID(new PageId(getIntValue(16)), i);
    }

    public byte[] selectRecord(RID rid)
    {
        short length = checkRID(rid);
        short offset = getSlotOffset(rid.slotno);
        byte record[] = new byte[length];
        System.arraycopy(data, offset, record, 0, length);
        return record;
    }

    public void updateRecord(RID rid, byte record[])
    {
        short length = checkRID(rid);
        if(record.length != length)
        {
            throw new IllegalArgumentException("Invalid record size");
        } else
        {
            short offset = getSlotOffset(rid.slotno);
            System.arraycopy(record, 0, data, offset, length);
            return;
        }
    }

    public void deleteRecord(RID rid)
    {
        short length = checkRID(rid);
        short offset = getSlotOffset(rid.slotno);
        short usedPtr = getShortValue(2);
        short newSpot = (short)(usedPtr + length);
        short size = (short)(offset - usedPtr);
        System.arraycopy(data, usedPtr, data, newSpot, size);
        short slotCnt = getShortValue(0);
        int i = 0;
        for(int n = 20; i < slotCnt; n += 4)
        {
            if(getSlotLength(i) != -1)
            {
                short chkoffset = getSlotOffset(i);
                if(chkoffset < offset)
                {
                    chkoffset += length;
                    setShortValue(chkoffset, n + 2);
                }
            }
            i++;
        }

        setShortValue(newSpot, 2);
        short freeSpace = getShortValue(4);
        freeSpace += length;
        setShortValue(freeSpace, 4);
        int slotpos = 20 + rid.slotno * 4;
        setShortValue((short)-1, slotpos);
        setShortValue((short)0, slotpos + 2);
    }

    public RID firstRecord()
    {
        short slotCnt = getShortValue(0);
        int i;
        for(i = 0; i < slotCnt; i++)
        {
            short length = getSlotLength(i);
            if(length != -1)
                break;
        }

        if(i == slotCnt)
            return null;
        else
            return new RID(new PageId(getIntValue(16)), i);
    }

    public boolean hasNext(RID curRid)
    {
        int curPid = getIntValue(16);
        short slotCnt = getShortValue(0);
        if(curRid.pageno.pid != curPid || curRid.slotno < 0 || curRid.slotno > slotCnt)
            throw new IllegalArgumentException("Invalid RID");
        int i;
        for(i = curRid.slotno + 1; i < slotCnt; i++)
        {
            short length = getSlotLength(i);
            if(length != -1)
                break;
        }

        return i != slotCnt;
    }

    public RID nextRecord(RID curRid)
    {
        int curPid = getIntValue(16);
        short slotCnt = getShortValue(0);
        if(curRid.pageno.pid != curPid || curRid.slotno < 0 || curRid.slotno > slotCnt)
            throw new IllegalArgumentException("Invalid RID");
        int i;
        for(i = curRid.slotno + 1; i < slotCnt; i++)
        {
            short length = getSlotLength(i);
            if(length != -1)
                break;
        }

        if(i == slotCnt)
            return null;
        else
            return new RID(new PageId(getIntValue(16)), i);
    }

    public void print()
    {
        short slotCnt = getShortValue(0);
        System.out.println("HFPage:");
        System.out.println("-------");
        System.out.println((new StringBuilder("  curPage   = ")).append(getIntValue(16)).toString());
        System.out.println((new StringBuilder("  prevPage  = ")).append(getIntValue(8)).toString());
        System.out.println((new StringBuilder("  nextPage  = ")).append(getIntValue(12)).toString());
        System.out.println((new StringBuilder("  slotCnt   = ")).append(slotCnt).toString());
        System.out.println((new StringBuilder("  usedPtr   = ")).append(getShortValue(2)).toString());
        System.out.println((new StringBuilder("  freeSpace = ")).append(getShortValue(4)).toString());
        System.out.println((new StringBuilder("  pageType  = ")).append(getShortValue(6)).toString());
        System.out.println("-------");
        int i = 0;
        for(int n = 20; i < slotCnt; n += 4)
        {
            System.out.println((new StringBuilder("slot #")).append(i).append(" offset = ").append(getShortValue(n)).toString());
            System.out.println((new StringBuilder("slot #")).append(i).append(" length = ").append(getShortValue(n + 2)).toString());
            i++;
        }

    }

    protected short checkRID(RID rid)
    {
        int curPid = getIntValue(16);
        short slotCnt = getShortValue(0);
        if(rid.pageno.pid != curPid || rid.slotno < 0 || rid.slotno > slotCnt)
            throw new IllegalArgumentException("Invalid RID");
        short recLen = getSlotLength(rid.slotno);
        if(recLen == -1)
            throw new IllegalArgumentException("Empty slot");
        else
            return recLen;
    }

    protected static final int SLOT_CNT = 0;
    protected static final int USED_PTR = 2;
    protected static final int FREE_SPACE = 4;
    protected static final int PAGE_TYPE = 6;
    protected static final int PREV_PAGE = 8;
    protected static final int NEXT_PAGE = 12;
    protected static final int CUR_PAGE = 16;
    public static final int HEADER_SIZE = 20;
    public static final int SLOT_SIZE = 4;
}
