// Generated by view binder compiler. Do not edit!
package com.deltagemunupuramv.dbms.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.deltagemunupuramv.dbms.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityViewStudentBinding implements ViewBinding {
  @NonNull
  private final CoordinatorLayout rootView;

  @NonNull
  public final TextView address;

  @NonNull
  public final TextView admissionDate;

  @NonNull
  public final Chip classChip;

  @NonNull
  public final TextView dateOfBirth;

  @NonNull
  public final FloatingActionButton deleteFab;

  @NonNull
  public final TextView disabilities;

  @NonNull
  public final LinearLayout disabilitiesSection;

  @NonNull
  public final FloatingActionButton editFab;

  @NonNull
  public final TextView emailAddress;

  @NonNull
  public final Chip genderChip;

  @NonNull
  public final Chip gradeChip;

  @NonNull
  public final TextView guardianContact;

  @NonNull
  public final TextView guardianName;

  @NonNull
  public final TextView guardianNic;

  @NonNull
  public final TextView guardianOccupation;

  @NonNull
  public final TextView medium;

  @NonNull
  public final TextView nameWithInitials;

  @NonNull
  public final TextView nicNumber;

  @NonNull
  public final TextView phoneNumber;

  @NonNull
  public final TextView previousSchools;

  @NonNull
  public final TextView religion;

  @NonNull
  public final TextView siblings;

  @NonNull
  public final LinearLayout siblingsSection;

  @NonNull
  public final ShapeableImageView studentAvatar;

  @NonNull
  public final TextView studentIndex;

  @NonNull
  public final TextView studentName;

  @NonNull
  public final TextView subjects;

  @NonNull
  public final Toolbar toolbar;

  @NonNull
  public final TextView whatsappNumber;

  private ActivityViewStudentBinding(@NonNull CoordinatorLayout rootView, @NonNull TextView address,
      @NonNull TextView admissionDate, @NonNull Chip classChip, @NonNull TextView dateOfBirth,
      @NonNull FloatingActionButton deleteFab, @NonNull TextView disabilities,
      @NonNull LinearLayout disabilitiesSection, @NonNull FloatingActionButton editFab,
      @NonNull TextView emailAddress, @NonNull Chip genderChip, @NonNull Chip gradeChip,
      @NonNull TextView guardianContact, @NonNull TextView guardianName,
      @NonNull TextView guardianNic, @NonNull TextView guardianOccupation, @NonNull TextView medium,
      @NonNull TextView nameWithInitials, @NonNull TextView nicNumber,
      @NonNull TextView phoneNumber, @NonNull TextView previousSchools, @NonNull TextView religion,
      @NonNull TextView siblings, @NonNull LinearLayout siblingsSection,
      @NonNull ShapeableImageView studentAvatar, @NonNull TextView studentIndex,
      @NonNull TextView studentName, @NonNull TextView subjects, @NonNull Toolbar toolbar,
      @NonNull TextView whatsappNumber) {
    this.rootView = rootView;
    this.address = address;
    this.admissionDate = admissionDate;
    this.classChip = classChip;
    this.dateOfBirth = dateOfBirth;
    this.deleteFab = deleteFab;
    this.disabilities = disabilities;
    this.disabilitiesSection = disabilitiesSection;
    this.editFab = editFab;
    this.emailAddress = emailAddress;
    this.genderChip = genderChip;
    this.gradeChip = gradeChip;
    this.guardianContact = guardianContact;
    this.guardianName = guardianName;
    this.guardianNic = guardianNic;
    this.guardianOccupation = guardianOccupation;
    this.medium = medium;
    this.nameWithInitials = nameWithInitials;
    this.nicNumber = nicNumber;
    this.phoneNumber = phoneNumber;
    this.previousSchools = previousSchools;
    this.religion = religion;
    this.siblings = siblings;
    this.siblingsSection = siblingsSection;
    this.studentAvatar = studentAvatar;
    this.studentIndex = studentIndex;
    this.studentName = studentName;
    this.subjects = subjects;
    this.toolbar = toolbar;
    this.whatsappNumber = whatsappNumber;
  }

  @Override
  @NonNull
  public CoordinatorLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityViewStudentBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityViewStudentBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_view_student, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityViewStudentBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.address;
      TextView address = ViewBindings.findChildViewById(rootView, id);
      if (address == null) {
        break missingId;
      }

      id = R.id.admissionDate;
      TextView admissionDate = ViewBindings.findChildViewById(rootView, id);
      if (admissionDate == null) {
        break missingId;
      }

      id = R.id.classChip;
      Chip classChip = ViewBindings.findChildViewById(rootView, id);
      if (classChip == null) {
        break missingId;
      }

      id = R.id.dateOfBirth;
      TextView dateOfBirth = ViewBindings.findChildViewById(rootView, id);
      if (dateOfBirth == null) {
        break missingId;
      }

      id = R.id.deleteFab;
      FloatingActionButton deleteFab = ViewBindings.findChildViewById(rootView, id);
      if (deleteFab == null) {
        break missingId;
      }

      id = R.id.disabilities;
      TextView disabilities = ViewBindings.findChildViewById(rootView, id);
      if (disabilities == null) {
        break missingId;
      }

      id = R.id.disabilitiesSection;
      LinearLayout disabilitiesSection = ViewBindings.findChildViewById(rootView, id);
      if (disabilitiesSection == null) {
        break missingId;
      }

      id = R.id.editFab;
      FloatingActionButton editFab = ViewBindings.findChildViewById(rootView, id);
      if (editFab == null) {
        break missingId;
      }

      id = R.id.emailAddress;
      TextView emailAddress = ViewBindings.findChildViewById(rootView, id);
      if (emailAddress == null) {
        break missingId;
      }

      id = R.id.genderChip;
      Chip genderChip = ViewBindings.findChildViewById(rootView, id);
      if (genderChip == null) {
        break missingId;
      }

      id = R.id.gradeChip;
      Chip gradeChip = ViewBindings.findChildViewById(rootView, id);
      if (gradeChip == null) {
        break missingId;
      }

      id = R.id.guardianContact;
      TextView guardianContact = ViewBindings.findChildViewById(rootView, id);
      if (guardianContact == null) {
        break missingId;
      }

      id = R.id.guardianName;
      TextView guardianName = ViewBindings.findChildViewById(rootView, id);
      if (guardianName == null) {
        break missingId;
      }

      id = R.id.guardianNic;
      TextView guardianNic = ViewBindings.findChildViewById(rootView, id);
      if (guardianNic == null) {
        break missingId;
      }

      id = R.id.guardianOccupation;
      TextView guardianOccupation = ViewBindings.findChildViewById(rootView, id);
      if (guardianOccupation == null) {
        break missingId;
      }

      id = R.id.medium;
      TextView medium = ViewBindings.findChildViewById(rootView, id);
      if (medium == null) {
        break missingId;
      }

      id = R.id.nameWithInitials;
      TextView nameWithInitials = ViewBindings.findChildViewById(rootView, id);
      if (nameWithInitials == null) {
        break missingId;
      }

      id = R.id.nicNumber;
      TextView nicNumber = ViewBindings.findChildViewById(rootView, id);
      if (nicNumber == null) {
        break missingId;
      }

      id = R.id.phoneNumber;
      TextView phoneNumber = ViewBindings.findChildViewById(rootView, id);
      if (phoneNumber == null) {
        break missingId;
      }

      id = R.id.previousSchools;
      TextView previousSchools = ViewBindings.findChildViewById(rootView, id);
      if (previousSchools == null) {
        break missingId;
      }

      id = R.id.religion;
      TextView religion = ViewBindings.findChildViewById(rootView, id);
      if (religion == null) {
        break missingId;
      }

      id = R.id.siblings;
      TextView siblings = ViewBindings.findChildViewById(rootView, id);
      if (siblings == null) {
        break missingId;
      }

      id = R.id.siblingsSection;
      LinearLayout siblingsSection = ViewBindings.findChildViewById(rootView, id);
      if (siblingsSection == null) {
        break missingId;
      }

      id = R.id.studentAvatar;
      ShapeableImageView studentAvatar = ViewBindings.findChildViewById(rootView, id);
      if (studentAvatar == null) {
        break missingId;
      }

      id = R.id.studentIndex;
      TextView studentIndex = ViewBindings.findChildViewById(rootView, id);
      if (studentIndex == null) {
        break missingId;
      }

      id = R.id.studentName;
      TextView studentName = ViewBindings.findChildViewById(rootView, id);
      if (studentName == null) {
        break missingId;
      }

      id = R.id.subjects;
      TextView subjects = ViewBindings.findChildViewById(rootView, id);
      if (subjects == null) {
        break missingId;
      }

      id = R.id.toolbar;
      Toolbar toolbar = ViewBindings.findChildViewById(rootView, id);
      if (toolbar == null) {
        break missingId;
      }

      id = R.id.whatsappNumber;
      TextView whatsappNumber = ViewBindings.findChildViewById(rootView, id);
      if (whatsappNumber == null) {
        break missingId;
      }

      return new ActivityViewStudentBinding((CoordinatorLayout) rootView, address, admissionDate,
          classChip, dateOfBirth, deleteFab, disabilities, disabilitiesSection, editFab,
          emailAddress, genderChip, gradeChip, guardianContact, guardianName, guardianNic,
          guardianOccupation, medium, nameWithInitials, nicNumber, phoneNumber, previousSchools,
          religion, siblings, siblingsSection, studentAvatar, studentIndex, studentName, subjects,
          toolbar, whatsappNumber);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
