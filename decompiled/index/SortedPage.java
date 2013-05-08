// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 5/8/2013 11:00:00 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package index;

import bufmgr.BufMgr;
import global.*;

// Referenced classes of package index:
//            DataEntry

class SortedPage extends Page
{

    public SortedPage()
    {
        a();
    }

    public SortedPage(Page page)
    {
        super(page.getData());
    }

    protected void a()
    {
        setShortValue((short)0, 0);
        setShortValue((short)1024, 2);
        setIntValue(-1, 4);
    }

    public short getEntryCount()
    {
        return getShortValue(0);
    }

    public short getFreeSpace()
    {
        return (short)(getShortValue(2) - (8 + getEntryCount() * 4));
    }

    public PageId getNextPage()
    {
        return new PageId(getIntValue(4));
    }

    public void setNextPage(PageId pageid)
    {
        setIntValue(pageid.pid, 4);
    }

    protected short _mthif(int i)
    {
        return getShortValue(8 + i * 4);
    }

    protected short a(int i)
    {
        return getShortValue(8 + i * 4 + 2);
    }

    protected void _mthdo(int i)
    {
        if(i < 0 || i > getEntryCount() - 1)
            throw new IllegalArgumentException("invalid slot number");
        else
            return;
    }

    public DataEntry getEntryAt(int i)
    {
        _mthdo(i);
        return new DataEntry(data, a(i));
    }

    public SearchKey getKeyAt(int i)
    {
        _mthdo(i);
        return new SearchKey(data, a(i));
    }

    public SearchKey getFirstKey()
    {
        if(getEntryCount() < 1)
            throw new IllegalStateException("page is empty");
        else
            return getKeyAt(0);
    }

    public boolean insertEntry(DataEntry dataentry)
    {
        short word0 = dataentry.getLength();
        short word1 = (short)(word0 + 4);
        if(word1 > getFreeSpace())
            throw new IllegalStateException("insufficient space");
        short word3 = getEntryCount();
        short word2;
        for(word2 = 0; word2 < word3; word2++)
            if(getKeyAt(word2).compareTo(dataentry.key) > 0)
                break;

        int i = 8 + word2 * 4;
        if(word2 < word3)
            System.arraycopy(data, i, data, i + 4, (word3 - word2) * 4);
        setShortValue(++word3, 0);
        short word4 = getShortValue(2);
        word4 -= word0;
        setShortValue(word4, 2);
        setShortValue(word0, i);
        setShortValue(word4, i + 2);
        dataentry.writeData(data, word4);
        return true;
    }

    public boolean deleteEntry(DataEntry dataentry)
    {
        short word1 = getEntryCount();
        short word0;
        for(word0 = 0; word0 < word1; word0++)
            if(getEntryAt(word0).equals(dataentry))
                break;

        if(word0 == word1)
            throw new IllegalArgumentException("entry doesn't exist");
        short word2 = (short)(8 + word0 * 4);
        short word3 = _mthif(word0);
        short word4 = a(word0);
        short word5 = getShortValue(2);
        short word6 = (short)(word5 + word3);
        System.arraycopy(data, word2 + 4, data, word2, (word1 - word0 - 1) * 4);
        System.arraycopy(data, word5, data, word6, word4 - word5);
        setShortValue(word6, 2);
        int i = 0;
        for(int j = 8; i < word1; j += 4)
        {
            short word7 = a(i);
            if(word7 < word4)
            {
                word7 += word3;
                setShortValue(word7, j + 2);
            }
            i++;
        }

        setShortValue(--word1, 0);
        return true;
    }

    public int findEntry(SearchKey searchkey)
    {
        return nextEntry(searchkey, -1);
    }

    public int nextEntry(SearchKey searchkey, int i)
    {
        short word0 = getEntryCount();
        for(int j = i + 1; j < word0; j++)
            if(getKeyAt(j).compareTo(searchkey) == 0)
                return j;

        return -1;
    }

    public PageId splitPage()
    {
        if(getEntryCount() < 2)
            throw new IllegalStateException("page contains fewer than two elements");
        SortedPage sortedpage = new SortedPage();
        PageId pageid = Minibase.BufferManager.newPage(sortedpage, 1);
        short word0 = getEntryCount();
        int i = word0 / 2;
        for(int j = i; j < word0; j++)
        {
            DataEntry dataentry = getEntryAt(i);
            sortedpage.insertEntry(dataentry);
            deleteEntry(dataentry);
        }

        Minibase.BufferManager.unpinPage(pageid, true);
        return pageid;
    }

    public void mergePage(PageId pageid)
    {
        SortedPage sortedpage = new SortedPage();
        Minibase.BufferManager.pinPage(pageid, sortedpage, false);
        short word0 = (short)(1016 - sortedpage.getFreeSpace());
        if(word0 > getFreeSpace())
        {
            Minibase.BufferManager.unpinPage(pageid, false);
            throw new IllegalArgumentException("insufficient space");
        }
        short word1 = sortedpage.getEntryCount();
        for(int i = 0; i < word1; i++)
            insertEntry(sortedpage.getEntryAt(i));

        Minibase.BufferManager.unpinPage(pageid, true);
        Minibase.BufferManager.freePage(pageid);
    }

    protected static final int _fldif = 0;
    protected static final int a = 2;
    protected static final int _fldnew = 4;
    protected static final int _fldfor = 8;
    protected static final int _fldint = 4;
    protected static final int _flddo = 1012;
}
