# community-Community Forum Project

## Introduction

A forum project with complete basic functions. The main features of the project are: registration based on email activation, password storage based on MD5 encryption and salting, and randomized CAPTCHA verification added to the login function. Realize the checking of login status and display different interfaces and functions for visitors and logged-in users. Realize permission control for different users and website statistics (UV, DAU), administrator can view website statistics and website monitoring information. Support users to upload avatars, realize the functions of posting posts, commenting posts, ranking hot posts, sending private messages and filtering sensitive words. Realize the function of liking and following and system notification. Support the function of global searching post information.

### Core functionality of the specific implementation

1. by issuing login credentials to the logged-in user, storing the login credentials in Redis to record the login status of the logged-in user, using an interceptor to check the login status, and using Spring Security to realize privilege control, which solves the defects of the statelessness of the http, and protects the specific resources that can only be used by logging in or with privileges. 2.
2. Use ThreadLocal to store user data in the current thread instead of session for distributed deployment. The user data is stored in the preHandle of the interceptor and the result of user authentication is constructed and stored in the SecurityContext, the user data is stored in the Model in the postHandle, and the user data is cleared in the afterCompletion. 3.
3. Use Redis collection data type to solve the function of "Like" and "Follow", adopt transaction management to ensure the correctness of the data, and adopt the strategy of "update the database before deleting the cache" to ensure the consistency between the database and the cache data. Redis is used to store CAPTCHA to solve the performance problem and the demand of CAPTCHA in distributed deployment. Use HyperLogLog of Redis to store daily UV and Bitmap to store DAU to realize the demand of website statistics. 4.
4. use Kafka as a message queue, push to the user by system notification after the user is liked, commented, or followed, synchronize to elasticsearch after the user publishes or deletes the post, and upload the long image to the cloud server after wk generates the long image to decouple the system and trim the peak.
5. use elasticsearch + ik participle plugin to realize global search function, when users publish, modify or delete posts, use Kafka message queue to asynchronously synchronize the posts to elasticsearch.
6. use distributed timer task Quartz to calculate post score to realize the business function of ranking hot posts.
7. For data that needs to be accessed frequently, such as user information, total number of posts, and single-page post list of hot posts, we use Caffeine local cache + Redis distributed cache to improve the server performance and realize high availability of the system.

### Core technologies

- Spring Boot, SSM
- Spring Boot, SSM, Redis, Kafka, Elasticsearch.
- Spring Security, Quartz, Caffeine

  
<img width="987" alt="Screenshot 2023-09-19 at 5 42 35 PM" src="https://github.com/SongtingY/nowcoderBackend/assets/94235734/1ca547f0-37c1-4512-a0fe-616d46d22ca8">
