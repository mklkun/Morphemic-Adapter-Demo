echo BEGIN "$variables_PA_TASK_NAME"

INSTANCE_NAME="$variables_INSTANCE_NAME"

################################################################################
### THIS PART IS IMAGE SPECIFIC. IF YOU NEED TO MODIFY SOMETHING, DO IT HERE ###
DOCKER_IMAGE=activeeon/mysql
PORT=3306
ROOT_PASSWORD="root"
USER="$variables_DB_USER"
PASSWORD="$variables_DB_PASSWORD"
DATABASE="$variables_DB_NAME"
VOLUME=db_data
# Check whether USER and PASSWORD have been well entered
if [ \( ! -z "$USER" -a -z "$PASSWORD" \) -o \( -z "$USER" -a ! -z "$PASSWORD" \) ]; then
    echo ERROR: "$variables_PA_JOB_NAME"_USER and "$variables_PA_JOB_NAME"_PASSWORD are used in junction. They should be either both entered or both blank.
    exit 1
fi
################################################################################

# Manually find a free random port to preserve it in case if docker (re)start (Pause/Resume)
GET_RANDOM_PORT(){
    read LOWERPORT UPPERPORT < /proc/sys/net/ipv4/ip_local_port_range
    while :
    do
        RND_PORT="`shuf -i $LOWERPORT-$UPPERPORT -n 1`"
        ss -lpn | grep -q ":$RND_PORT " || break
    done
    echo $RND_PORT
}

echo "Pulling "$variables_PA_JOB_NAME" image"
sudo docker pull "$DOCKER_IMAGE"

sudo docker volume create "$VOLUME"

################################################################################
### THIS PART IS IMAGE SPECIFIC. IF YOU NEED TO MODIFY SOMETHING, DO IT HERE ###
DB_PORT=$(GET_RANDOM_PORT)
echo "Running $INSTANCE_NAME container"
if [ "$DATABASE" == null ] && [ "$USER" == null ]; then
    sudo docker run --name "$INSTANCE_NAME" -p $DB_PORT:$PORT -e MYSQL_ROOT_PASSWORD="$ROOT_PASSWORD" -v "$VOLUME":/var/lib/mysql -d "$DOCKER_IMAGE"
elif [ "$DATABASE" == null ] && [ "$USER" != null ]; then
    sudo docker run --name "$INSTANCE_NAME" -p $DB_PORT:$PORT -e MYSQL_ROOT_PASSWORD="$ROOT_PASSWORD" -e MYSQL_USER="$USER" -e MYSQL_PASSWORD="$PASSWORD" -v "$VOLUME":/var/lib/mysql -d "$DOCKER_IMAGE"
elif [ "$DATABASE" != null ] && [ "$USER" == null ]; then
    sudo docker run --name "$INSTANCE_NAME" -p $DB_PORT:$PORT -e MYSQL_ROOT_PASSWORD="$ROOT_PASSWORD" -e MYSQL_DATABASE="$DATABASE" -v "$VOLUME":/var/lib/mysql -d "$DOCKER_IMAGE"
else
    sudo docker run --name "$INSTANCE_NAME" -p $DB_PORT:$PORT -e MYSQL_ROOT_PASSWORD="$ROOT_PASSWORD" -e MYSQL_USER="$USER" -e MYSQL_PASSWORD="$PASSWORD" -e MYSQL_DATABASE="$DATABASE" -v "$VOLUME":/var/lib/mysql -d "$DOCKER_IMAGE"
fi
################################################################################

if [ "$(sudo docker ps --format '{{.Names}}' | grep "^$INSTANCE_NAME$")" ]; then
    RUNNING=$(sudo docker inspect --format="{{ .State.Running }}" "$INSTANCE_NAME" 2> /dev/null)
    if [ "$RUNNING" == "true" ]; then
        echo $INSTANCE_NAME started successfully.
        IP_ADDR=$( curl api.ipify.org )
        echo "$IP_ADDR" > $INSTANCE_NAME"_dbipaddr"
        echo "$DB_PORT" > $INSTANCE_NAME"_dbport"
		echo "MySQL adress is http://$IP_ADDR:$DB_PORT"

    fi
else
    echo ERROR: $INSTANCE_NAME could not be started properly.
    exit 1
fi