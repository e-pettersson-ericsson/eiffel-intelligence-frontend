import os


if __name__ == '__main__':
    print("Preparing a release")
    print(os.getenv("TRAVIS_COMMIT_MESSAGE"))