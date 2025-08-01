// Generated by view binder compiler. Do not edit!
package com.deltagemunupuramv.dbms.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.deltagemunupuramv.dbms.R;
import com.google.android.material.card.MaterialCardView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ItemFeatureBinding implements ViewBinding {
  @NonNull
  private final MaterialCardView rootView;

  @NonNull
  public final TextView featureDescription;

  @NonNull
  public final ImageView featureIcon;

  @NonNull
  public final TextView featureTitle;

  private ItemFeatureBinding(@NonNull MaterialCardView rootView,
      @NonNull TextView featureDescription, @NonNull ImageView featureIcon,
      @NonNull TextView featureTitle) {
    this.rootView = rootView;
    this.featureDescription = featureDescription;
    this.featureIcon = featureIcon;
    this.featureTitle = featureTitle;
  }

  @Override
  @NonNull
  public MaterialCardView getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemFeatureBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemFeatureBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_feature, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemFeatureBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.featureDescription;
      TextView featureDescription = ViewBindings.findChildViewById(rootView, id);
      if (featureDescription == null) {
        break missingId;
      }

      id = R.id.featureIcon;
      ImageView featureIcon = ViewBindings.findChildViewById(rootView, id);
      if (featureIcon == null) {
        break missingId;
      }

      id = R.id.featureTitle;
      TextView featureTitle = ViewBindings.findChildViewById(rootView, id);
      if (featureTitle == null) {
        break missingId;
      }

      return new ItemFeatureBinding((MaterialCardView) rootView, featureDescription, featureIcon,
          featureTitle);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
