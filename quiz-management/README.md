# Quiz Management: Spray - Akka - Slick

Build a REST api with Spray and Akka and Slick

## Run the service:
```
> sbt run
```

The service runs on port 5000 by default.

## Usage

### Create a quiz
```
curl -v -H "Content-Type: application/json" \
     -X POST http://localhost:5000/quizzes \
     -d '{"id": "test", "question": "is scala cool?", "correctAnswer": "YES!"}'
```

### Update a quiz
```
curl -v -H "Content-Type: application/json" \
     -X POST http://localhost:5000/quizzes/test \
     -d '{"id": "test", "question": "is scala cool?", "correctAnswer": "NO!"}'
```

### Delete a quiz
```
curl -v -X DELETE http://localhost:5000/quizzes/test
```

### Get a quiz by id
```
curl -v http://localhost:5000/quizzes/test
```

### Get all
```
curl -v http://localhost:5000/quizzes?from=0&pageSize=10&sortBy=id&asc=true&filter=correctAnswer&filterValue=YESfrom=0&pageSize=2
```
