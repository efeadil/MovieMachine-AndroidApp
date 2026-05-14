package com.example.moviemachine.view;

/**
 * Bu sınıf, film detay sayfasındaki oyuncu kadrosunu listeler.
 * Oyuncuların isimlerini ve profil fotoğraflarını RecyclerView içinde gösterir.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.moviemachine.Model.CastMember;
import com.example.moviemachine.R;
import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private Context context;
    private List<CastMember> castList;

    public CastAdapter(Context context, List<CastMember> castList) {
        this.context = context;
        this.castList = castList;
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_cast, parent, false);
        return new CastViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        CastMember actor = castList.get(position);
        holder.tvCastName.setText(actor.getName());

        // TMDB'den GERÇEK oyuncu fotoğrafını çekiyoruz
        if (actor.getProfilePath() != null && !actor.getProfilePath().equals("null")) {
            String imageUrl = "https://image.tmdb.org/t/p/w200" + actor.getProfilePath();
            Glide.with(context).load(imageUrl).circleCrop().into(holder.imgCastProfile);
        } else {
            // Eğer oyuncunun resmi yoksa boş bir ikon göster
            Glide.with(context).load(R.drawable.ic_person).circleCrop().into(holder.imgCastProfile);
        }
    }

    @Override
    public int getItemCount() { return castList.size(); }

    static class CastViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCastProfile;
        TextView tvCastName;

        public CastViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCastProfile = itemView.findViewById(R.id.imgCastProfile);
            tvCastName = itemView.findViewById(R.id.tvCastName);
        }
    }
}