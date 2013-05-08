// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/8/2013 10:58:53 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   HeapFile.java

package heap;

import bufmgr.BufMgr;
import diskmgr.DiskMgr;
import global.*;

// Referenced classes of package heap:
//            DirPage, DataPage, HeapScan

public class HeapFile
    implements GlobalConst
{

    public HeapFile(String name)
    {
        fileName = name;
        boolean exists = false;
        if(name != null)
        {
            isTemp = false;
            headId = Minibase.DiskManager.get_file_entry(name);
            if(headId != null)
                exists = true;
        } else
        {
            isTemp = true;
        }
        if(!exists)
        {
            DirPage dirPage = new DirPage();
            headId = Minibase.BufferManager.newPage(dirPage, 1);
            dirPage.setCurPage(headId);
            Minibase.BufferManager.unpinPage(headId, true);
            if(!isTemp)
                Minibase.DiskManager.add_file_entry(name, headId);
        }
    }

    protected void finalize()
        throws Throwable
    {
        if(isTemp)
            deleteFile();
    }

    public void deleteFile()
    {
        PageId dirId = new PageId(headId.pid);
        DirPage dirPage = new DirPage();
        PageId nextId;
        for(; dirId.pid != -1; dirId = nextId)
        {
            Minibase.BufferManager.pinPage(dirId, dirPage, false);
            int count = dirPage.getEntryCnt();
            for(int i = 0; i < count; i++)
            {
                PageId pageId = dirPage.getPageId(i);
                Minibase.BufferManager.freePage(pageId);
            }

            nextId = dirPage.getNextPage();
            Minibase.BufferManager.unpinPage(dirId, false);
            Minibase.BufferManager.freePage(dirId);
        }

        if(!isTemp)
            Minibase.DiskManager.delete_file_entry(fileName);
    }

    public RID insertRecord(byte record[])
    {
        if(record.length > 1004)
        {
            throw new IllegalArgumentException("Record too large");
        } else
        {
            PageId pageno = getAvailPage(record.length + 4);
            DataPage page = new DataPage();
            Minibase.BufferManager.pinPage(pageno, page, false);
            RID rid = page.insertRecord(record);
            short freecnt = page.getFreeSpace();
            Minibase.BufferManager.unpinPage(pageno, true);
            updateEntry(pageno, 1, freecnt);
            return rid;
        }
    }

    public byte[] selectRecord(RID rid)
    {
        DataPage page;
        page = new DataPage();
        Minibase.BufferManager.pinPage(rid.pageno, page, false);
        byte abyte0[];
        try
        {
            abyte0 = page.selectRecord(rid);
        }
        catch(IllegalArgumentException exc)
        {
            throw exc;
        }
        Minibase.BufferManager.unpinPage(rid.pageno, false);
        return abyte0;
        Exception exception;
        exception;
        Minibase.BufferManager.unpinPage(rid.pageno, false);
        throw exception;
    }

    public void updateRecord(RID rid, byte newRecord[])
    {
        DataPage page = new DataPage();
        Minibase.BufferManager.pinPage(rid.pageno, page, false);
        try
        {
            page.updateRecord(rid, newRecord);
            Minibase.BufferManager.unpinPage(rid.pageno, true);
        }
        catch(IllegalArgumentException exc)
        {
            Minibase.BufferManager.unpinPage(rid.pageno, false);
            throw exc;
        }
    }

    public void deleteRecord(RID rid)
    {
        DataPage page = new DataPage();
        Minibase.BufferManager.pinPage(rid.pageno, page, false);
        try
        {
            page.deleteRecord(rid);
            short freecnt = page.getFreeSpace();
            Minibase.BufferManager.unpinPage(rid.pageno, true);
            updateEntry(rid.pageno, -1, freecnt);
        }
        catch(IllegalArgumentException exc)
        {
            Minibase.BufferManager.unpinPage(rid.pageno, false);
            throw exc;
        }
    }

    public int getRecCnt()
    {
        int cnt = 0;
        PageId dirId = new PageId(headId.pid);
        DirPage dirPage = new DirPage();
        PageId nextId;
        for(; dirId.pid != -1; dirId = nextId)
        {
            Minibase.BufferManager.pinPage(dirId, dirPage, false);
            int count = dirPage.getEntryCnt();
            for(int i = 0; i < count; i++)
                cnt += dirPage.getRecCnt(i);

            nextId = dirPage.getNextPage();
            Minibase.BufferManager.unpinPage(dirId, false);
        }

        return cnt;
    }

    public HeapScan openScan()
    {
        return new HeapScan(this);
    }

    public String toString()
    {
        return fileName;
    }

    protected PageId getAvailPage(int reclen)
    {
        PageId freeId = null;
        PageId dirId = new PageId(headId.pid);
        DirPage dirPage = new DirPage();
        PageId nextId;
        for(; freeId == null && dirId.pid != -1; dirId = nextId)
        {
            Minibase.BufferManager.pinPage(dirId, dirPage, false);
            int count = dirPage.getEntryCnt();
            for(int i = 0; i < count; i++)
            {
                if(dirPage.getFreeCnt(i) < reclen + 4)
                    continue;
                freeId = dirPage.getPageId(i);
                break;
            }

            nextId = dirPage.getNextPage();
            Minibase.BufferManager.unpinPage(dirId, false);
        }

        if(freeId == null)
            freeId = insertPage();
        return freeId;
    }

    protected int findEntry(PageId pageno, PageId dirId, DirPage dirPage)
    {
        dirId.pid = headId.pid;
        do
        {
            Minibase.BufferManager.pinPage(dirId, dirPage, false);
            int count = dirPage.getEntryCnt();
            for(int i = 0; i < count; i++)
                if(pageno.pid == dirPage.getPageId(i).pid)
                    return i;

            PageId nextId = dirPage.getNextPage();
            Minibase.BufferManager.unpinPage(dirId, false);
            dirId.pid = nextId.pid;
        } while(true);
    }

    protected void updateEntry(PageId pageno, int deltaRec, int freecnt)
    {
        PageId dirId = new PageId();
        DirPage dirPage = new DirPage();
        int index = findEntry(pageno, dirId, dirPage);
        int reccnt = dirPage.getRecCnt(index) + deltaRec;
        if(reccnt < 1)
        {
            deletePage(pageno, dirId, dirPage, index);
        } else
        {
            dirPage.setRecCnt(index, (short)reccnt);
            dirPage.setFreeCnt(index, (short)freecnt);
            Minibase.BufferManager.unpinPage(dirId, true);
        }
    }

    protected PageId insertPage()
    {
        int count = 0;
        PageId dirId = new PageId(headId.pid);
        DirPage dirPage = new DirPage();
        do
        {
            Minibase.BufferManager.pinPage(dirId, dirPage, false);
            count = dirPage.getEntryCnt();
            if(count < 125)
                break;
            PageId nextId = dirPage.getNextPage();
            if(nextId.pid == -1)
            {
                DirPage newDirPage = new DirPage();
                PageId newDirId = Minibase.BufferManager.newPage(newDirPage, 1);
                newDirPage.setCurPage(newDirId);
                dirPage.setNextPage(newDirId);
                newDirPage.setPrevPage(dirId);
                Minibase.BufferManager.unpinPage(dirId, true);
                dirId = newDirId;
                dirPage = newDirPage;
                count = 0;
                break;
            }
            Minibase.BufferManager.unpinPage(dirId, false);
            dirId = nextId;
        } while(true);
        DataPage dataPage = new DataPage();
        PageId dataId = Minibase.BufferManager.newPage(dataPage, 1);
        dataPage.setCurPage(dataId);
        dirPage.setPageId(count, dataId);
        dirPage.setRecCnt(count, (short)0);
        dirPage.setFreeCnt(count, dataPage.getFreeSpace());
        dirPage.setEntryCnt((short)(++count));
        Minibase.BufferManager.unpinPage(dataId, true);
        Minibase.BufferManager.unpinPage(dirId, true);
        return dataId;
    }

    protected void deletePage(PageId pageno, PageId dirId, DirPage dirPage, int index)
    {
        Minibase.BufferManager.freePage(pageno);
        dirPage.compact(index);
        short count = dirPage.getEntryCnt();
        if(count == 1 && dirId.pid != headId.pid)
        {
            DirPage page = new DirPage();
            PageId prevId = dirPage.getPrevPage();
            PageId nextId = dirPage.getNextPage();
            if(prevId.pid != -1)
            {
                Minibase.BufferManager.pinPage(prevId, page, false);
                page.setNextPage(nextId);
                Minibase.BufferManager.unpinPage(prevId, true);
            }
            if(nextId.pid != -1)
            {
                Minibase.BufferManager.pinPage(nextId, page, false);
                page.setPrevPage(prevId);
                Minibase.BufferManager.unpinPage(nextId, true);
            }
            Minibase.BufferManager.unpinPage(dirId, false);
            Minibase.BufferManager.freePage(dirId);
        } else
        {
            dirPage.setEntryCnt(--count);
            Minibase.BufferManager.unpinPage(dirId, true);
        }
    }

    protected static final short DIR_PAGE = 10;
    protected static final short DATA_PAGE = 11;
    protected String fileName;
    protected boolean isTemp;
    protected PageId headId;
}
