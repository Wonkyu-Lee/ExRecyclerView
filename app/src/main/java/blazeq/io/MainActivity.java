package blazeq.io;

import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private static int ITEM_COUNT = 100;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private LinkedList<Item> mItems = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);
        mItems = createItems();
        mAdapter = new RecyclerViewAdapter(mItems);
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        new ItemTouchHelper(new ItemTouchHelperCallback()).attachToRecyclerView(mRecyclerView);
    }

    private static class Item {
        final int id;
        final int fgColor;
        final int bgColor;

        Item(int id) {
            this.id = id;
            fgColor = getFgColor(id);
            bgColor = getBgColor(id);
        }

        static int getFgColor(int id) {
            float h = 360.f/(ITEM_COUNT - 1) * id;
            float[] hsv = {h, .5f, .5f};
            return ColorUtils.HSLToColor(hsv);
        }

        static int getBgColor(int id) {
            float h = 360.f/(ITEM_COUNT - 1) * id + 180.f;
            while (h > 360.f) h -= 360.f;
            float[] hsv = {h, .5f, .5f};
            return ColorUtils.HSLToColor(hsv);
        }
    }

    private class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
        @Override
        public int getMovementFlags(
                @NonNull RecyclerView recyclerView,
                @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(
                    ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT|ItemTouchHelper.UP|ItemTouchHelper.DOWN,
                    0
            );
        }

        @Override
        public boolean onMove(
                @NonNull RecyclerView recyclerView,
                @NonNull RecyclerView.ViewHolder viewHolder1,
                @NonNull RecyclerView.ViewHolder viewHolder2) {
            int pos1 = viewHolder1.getAdapterPosition();
            int pos2 = viewHolder2.getAdapterPosition();
            Log.d("MOVE", String.format("pos1: %d, pos2: %d", pos1, pos2));

            if (pos1 == pos2) {
                return false;
            }
            Item item = mItems.remove(pos1);
            mItems.add(pos2, item);
            mAdapter.notifyItemMoved(pos1, pos2);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {}
    }

    private LinkedList<Item> createItems() {
        LinkedList<Item> items = new LinkedList<>();
        for (int i = 0; i < ITEM_COUNT; ++i) {
            items.add(new Item(i));
        }
        return items;
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        public final View itemView;
        public final TextView tvText;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.tvText = itemView.findViewById(R.id.tv_text);
        }
    }

    private static class RecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        final LinkedList<Item> items;

        RecyclerViewAdapter(LinkedList<Item> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View itemView = inflater.inflate(R.layout.item, null);
            int width = viewGroup.getMeasuredWidth() / 2;
            itemView.setMinimumWidth(width);
            itemView.setMinimumHeight(width);
            return new ItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
            Item item = items.get(i);
            itemViewHolder.itemView.setBackgroundColor(item.bgColor);
            itemViewHolder.tvText.setTextColor(item.fgColor);
            itemViewHolder.tvText.setText(String.valueOf(item.id));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}
