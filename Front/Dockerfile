# Use a base image with Node.js installed
FROM node:14-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the package.json and package-lock.json files to the container
COPY package.json ./
COPY ./src ./src

# Install the application dependencies
RUN npm install

# Expose the port that your application listens on
EXPOSE 80

# Set the entrypoint command to run the application
CMD ["npm", "start"]
