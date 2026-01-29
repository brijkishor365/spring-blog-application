**Generate RSA Key Configuration (Best Practice)**
> openssl genrsa -out private.pem 2048
> 
> openssl rsa -in private.pem -pubout -out public.pem

**Application Setup Detail:**
> Create database in postgress DB and Import DB file.
> 
> Use MongoDB for JWT Token blacklisting
> 
> Change DB and port details in application.yml file in req
> 
> Import Postman collection and use it