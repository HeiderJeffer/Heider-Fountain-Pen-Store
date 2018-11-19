package com.example.dellxps15.roomwordsample2;

import android.text.Spanned;
import android.text.SpannedString;

import static junit.framework.Assert.assertEquals;
import com.example.dellxps15.roomwordsample2.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;



public class FountainUnitTest {
    private MainActivity mainActivity;
    Double d = MainActivity.getRandomDoubleBetweenRange(800.0, 900.0);
    int id = d.intValue();

    @Before
    public void setUp() {
        this.mainActivity = spy(new MainActivity());
    }

    @Test
    public void checkProductInsert(){

        Products p = new Products(  id,
                "CHECK1",
                "Gold-tip Mont Blanc (Black and Gold) Made in Switzerland",
                678,
                "a1");

        // 1. create mock
        MainActivity ma = mock(MainActivity.class);

        // 2. tell the mock how to behave
        when(ma.newPop(p)).thenReturn(true);

        // 3. use the mock
        assertEquals(true, ma.newPop(p));

    }

    @Test
    public void checkProductsDelete(){
        // 1. create mock
        MainActivity ma = mock(MainActivity.class);

        // 2. tell the mock how to behave
        when(ma.oldPop()).thenReturn(true);

        // 3. use the mock
        assertEquals(true, ma.oldPop());
    }
}
