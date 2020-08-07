echo BEGIN "$variables_PA_TASK_NAME"

INSTANCE_NAME="$variables_INSTANCE_NAME"

################################################################################
### THIS PART IS IMAGE SPECIFIC. IF YOU NEED TO MODIFY SOMETHING, DO IT HERE ###
DOCKER_IMAGE=wordpress
PORT=80
ROOT_PASSWORD="root"
DB_IPADDR="$variables_DB_IPADDR"
DB_PORT="$variables_DB_PORT"
DB_USER="$variables_DB_USER"
DB_PASSWORD="$variables_DB_PASSWORD"
DB_NAME="$variables_DB_NAME"
#DB_NET="$variables_DB_NET"
# Check whether USER and PASSWORD have been well entered
if [ \( ! -z "$DB_USER" -a -z "$DB_PASSWORD" \) -o \( -z "$DB_USER" -a ! -z "$DB_PASSWORD" \) ]; then
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

################################################################################
### THIS PART IS IMAGE SPECIFIC. IF YOU NEED TO MODIFY SOMETHING, DO IT HERE ###
WP_PORT=$(GET_RANDOM_PORT)
echo "Running $INSTANCE_NAME container"
sudo docker run --name "$INSTANCE_NAME" -p $WP_PORT:$PORT  -e WORDPRESS_DB_HOST=$DB_IPADDR:$DB_PORT -e WORDPRESS_DB_USER=$DB_USER -e WORDPRESS_DB_PASSWORD=$DB_PASSWORD -e WORDPRESS_DB_NAME=$DB_NAME -d $DOCKER_IMAGE
################################################################################

if [ "$(sudo docker ps --format '{{.Names}}' | grep "^$INSTANCE_NAME$")" ]; then
    RUNNING=$(sudo docker inspect --format="{{ .State.Running }}" $INSTANCE_NAME 2> /dev/null)
    if [ "$RUNNING" == "true" ]; then
        echo $INSTANCE_NAME started successfully on port: $WP_PORT
        IP_ADDR=$( curl api.ipify.org )
		echo "To access it go to http://$IP_ADDR:$WP_PORT"
    fi
else
    echo ERROR: $INSTANCE_NAME could not be started properly.
    exit 1
fi