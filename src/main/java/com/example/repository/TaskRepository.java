package com.example.repository;

import com.example.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

// By extending MongoRepository, we get a bunch of CRUD methods for free!
// We specify it's a repository for 'Task' objects, and the ID is of type 'String'.
public interface TaskRepository extends MongoRepository<Task, String> {

    // Spring Data MongoDB will automatically create the query for this method
    // based on the method name. This will find tasks where the 'name' field
    // contains the given search string. This is for the "find tasks by name" endpoint.
    List<Task> findByNameContaining(String name);
}