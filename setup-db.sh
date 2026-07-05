#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== BG-Tracker MySQL Docker Setup ===${NC}"

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Docker is not installed. Please install Docker first.${NC}"
    exit 1
fi

# Check if Docker daemon is running
if ! docker info &> /dev/null; then
    echo -e "${RED}Docker daemon is not running. Please start Docker.${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Docker is running${NC}"

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}docker-compose is not installed. Please install docker-compose first.${NC}"
    exit 1
fi

echo -e "${GREEN}✓ docker-compose is available${NC}"

# Stop existing container if running
if docker ps | grep -q bg-tracker-mysql; then
    echo -e "${YELLOW}Stopping existing bg-tracker-mysql container...${NC}"
    docker-compose down
fi

# Start the containers
echo -e "${YELLOW}Starting MySQL container...${NC}"
docker-compose up -d

# Wait for MySQL to be ready
echo -e "${YELLOW}Waiting for MySQL to be ready...${NC}"
sleep 5

# Check if container is running
if docker ps | grep -q bg-tracker-mysql; then
    echo -e "${GREEN}✓ MySQL container is running!${NC}"
    echo -e "${GREEN}Database setup complete!${NC}"
    echo ""
    echo -e "${YELLOW}Connection details:${NC}"
    echo "  Host: localhost"
    echo "  Port: 3306"
    echo "  Database: bg_tracker"
    echo "  Root User: root"
    echo "  Root Password: rootpassword"
    echo "  App User: bg_user"
    echo "  App Password: bg_password"
    echo ""
    echo -e "${YELLOW}Useful commands:${NC}"
    echo "  View logs: docker-compose logs -f mysql"
    echo "  Stop: docker-compose down"
    echo "  Stop and remove data: docker-compose down -v"
else
    echo -e "${RED}Failed to start MySQL container.${NC}"
    echo "For more details, run: docker-compose logs mysql"
    exit 1
fi

