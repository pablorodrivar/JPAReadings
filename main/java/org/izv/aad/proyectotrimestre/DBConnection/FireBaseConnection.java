package org.izv.aad.proyectotrimestre.DBConnection;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.izv.aad.proyectotrimestre.POJO.Author;
import org.izv.aad.proyectotrimestre.POJO.Readings;
import java.util.HashMap;
import java.util.Map;


public class FireBaseConnection {
    public static FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    public static FirebaseStorage storage;
    public static StorageReference storageRef;

    public FireBaseConnection(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }
    public DatabaseReference getDatabaseReference(String path) {
        return firebaseDatabase.getReference(path);
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }


    public String getEmail(){
        return firebaseUser.getEmail();
    }


    public void saveReading(Readings reading) {
        Map<String, Object> saveUser = new HashMap<>(); // SIEMPRE ES NULL LA FIREBASE KEY, HAY Q VER POR QUE
        String fireBaseKey = reading.getFireBaseKey();
        saveUser.put("/user/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/readings/" + fireBaseKey + "/", reading.toMap());
        databaseReference.updateChildren(saveUser).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    public void saveAuthor(Author author) {
        Map<String, Object> saveUser = new HashMap<>();
        saveUser.put("/user/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/authors/" + author.getFireBaseKey() + "/", author.toMap());
        databaseReference.updateChildren(saveUser).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    public void deleteReading(Readings reading){
        databaseReference.child("/user/" + firebaseAuth.getCurrentUser().getUid() + "/readings/" + reading.getFireBaseKey()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
            }
        });
    }

    public void deleteAuthor(Author author){
        databaseReference.child("/user/" + firebaseAuth.getCurrentUser().getUid() + "/authors/" + author.getFireBaseKey()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
            }
        });
    }

    public void signOut(){
        firebaseAuth.signOut();
    }
}
