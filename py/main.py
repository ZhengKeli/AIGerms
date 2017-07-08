import core
import io


def initialize():
    core.initialize()
    io.write_int(io.STU_SUCCEED)
    io.flush_stdout()


def finalize():
    core.finalize()
    io.write_int(io.STU_SUCCEED)
    io.flush_stdout()


def run_actor():
    val_feel_nutrient = io.read_vector_list()
    val_feel_germ = io.read_vector_list()
    val_feel_energy = io.read_float_list()
    val_act_velocity = core.run_actor(val_feel_nutrient, val_feel_germ, val_feel_energy)
    io.write_int(io.STU_SUCCEED)
    io.write_vector_list(val_act_velocity)
    io.flush_stdout()


def train_critic():
    val_feel_nutrient = io.read_vector_list()
    val_feel_germ = io.read_vector_list()
    val_feel_energy = io.read_float_list()
    val_act_velocity = io.read_vector_list()
    val_real_loss = io.read_float_list()
    core.train_critic(val_feel_nutrient, val_feel_germ, val_feel_energy, val_act_velocity, val_real_loss)
    io.write_int(io.STU_SUCCEED)
    io.flush_stdout()


def train_actor():
    val_feel_nutrient = io.read_vector_list()
    val_feel_germ = io.read_vector_list()
    val_feel_energy = io.read_float_list()
    core.train_actor(val_feel_nutrient, val_feel_germ, val_feel_energy)
    io.write_int(io.STU_SUCCEED)
    io.flush_stdout()


def main():
    while True:
        com = io.read_int()
        if com == io.COM_INITIALIZE:
            initialize()
        elif com == io.COM_FINALIZE:
            finalize()
            break
        elif com == io.COM_RUN_ACTOR:
            run_actor()
        elif com == io.COM_TRAIN_CRITIC:
            train_critic()
        elif com == io.COM_TRAIN_ACTOR:
            train_actor()
        else:
            print("the command is wrong!")
            break


main()
