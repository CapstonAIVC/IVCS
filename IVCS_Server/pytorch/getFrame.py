import cv2
import sys

if __name__ == '__main__':
    cap = cv2.VideoCapture("http://cctvsec.ktict.co.kr/2/+QtAeiKzuGO4duhtc0LFpmIHgo8RG5gi6MFRCCn+gYVt+JoW18o61aG5IBhJxi+IrDiyL78n/3sl073+QNUpXA==")
    if (cap.isOpened() == False):
        print('!!! Unable to open URL')
        sys.exit(-1)

    # retrieve FPS and calculate how long to wait between each frame to be display
    fps = cap.get(cv2.CAP_PROP_FPS)
    # wait_ms = int(1000/fps) ## fps/2
    print('FPS:', fps)
    seq=1

    while(True):
        # read one frame
        ret, frame = cap.read()

        img = cv2.resize(frame, (160, 120))
        if seq % 30 == 0:
            cv2.imwrite("./test_path/{}.jpg".format(seq), img)
        seq += 1

        # display frame
        # cv2.imshow('frame',frame)
        # if cv2.waitKey(wait_ms) & 0xFF == ord('q'):
        #     break

    cap.release()