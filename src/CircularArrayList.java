import java.util.ArrayList;

public class CircularArrayList<Integer> extends ArrayList<Integer>
{

    @Override
    public Integer get(int index)
    {
        if (index == -1)
        {
            index = size()-1;
        }

        else if (index == size())
        {
            index = 0;
        }
        return super.get(index%size());
    }

}
