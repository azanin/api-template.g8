# My Giter 8 Template to build APIs

Type `sbt new azanin/api-template.g8`

## Features

This template configures a health check endpoint with:
- Tapir
- Swagger
- Http4s
- IT tests using a test container
- CI using Github actions
- Publish Docker images in Github container registry
- Scala Steward + Mergify configuration

Remember to configure STEWARD and GHRC secret to publishing images. 

The reference here: https://docs.github.com/en/packages/guides/pushing-and-pulling-docker-images 
