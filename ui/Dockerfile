# Use an official Node.js image as the base image
FROM node:21.6.2 as build

# Set the working directory in the container
WORKDIR /app

# Copy package.json and yarn.lock (if exists) to the working directory
COPY package.json yarn.lock* ./

# Install dependencies
RUN yarn install --frozen-lockfile

# Copy the entire project to the working directory
COPY . .

# Build the React app
RUN yarn build

# Stage 2 - Production environment
FROM nginx:stable-alpine

# Copy the built React app from the build stage
COPY --from=build /app/build /usr/share/nginx/html

# Expose port 80
EXPOSE 80

# Set the APP_PORT environment variable for the nginx container
ENV APP_PORT=8080

# Command to run nginx
CMD ["nginx", "-g", "daemon off;"]